package net.vicp.biggee.kotlin.img.tif

import com.github.jaiimageio.impl.plugins.tiff.*
import com.github.jaiimageio.plugins.tiff.BaselineTIFFTagSet
import com.github.jaiimageio.plugins.tiff.TIFFColorConverter
import com.github.jaiimageio.plugins.tiff.TIFFCompressor
import com.github.jaiimageio.plugins.tiff.TIFFImageWriteParam
import org.opencv.core.Mat
import org.opencv.core.Size
import org.opencv.imgcodecs.Imgcodecs
import org.opencv.imgproc.Imgproc
import java.io.File
import java.io.IOException
import java.util.*
import javax.imageio.IIOException
import javax.imageio.IIOImage
import javax.imageio.ImageIO
import javax.imageio.metadata.IIOMetadata
import javax.imageio.stream.ImageOutputStream

class Main {
    companion object{
        @JvmStatic
        fun main(args: Array<String>) {
            if(args.isNullOrEmpty()){
                return;
            }
            System.loadLibrary("opencv_java412")
            val img=Imgcodecs.imread(args[0])
            val tmp1= Mat()
            val tmp2=Mat()
            Imgproc.GaussianBlur(img,tmp1,Size(3.0,3.0),0.0)
            Imgcodecs.imwrite("1.jpg",tmp1)
            Imgproc.cvtColor(tmp1,tmp2,Imgproc.COLOR_BGR2GRAY)
            Imgcodecs.imwrite("2.jpg",tmp2)
            Imgproc.threshold(tmp2,tmp1,254.0,255.0,Imgproc.THRESH_BINARY)
            Imgcodecs.imwrite("3.jpg",tmp1)
            val tmpfname="${args[0]}_YH_PROC.tif"
            Imgcodecs.imwrite(tmpfname,tmp1)
            val javaImgTmp1= ImageIO.read(File(tmpfname))

            val output=File("4.tif")
            if(output.exists()){
                output.delete()
            }
            val stream=ImageIO.createImageOutputStream(output)

            try {
                val writer = TIFFImageWriter(TIFFImageWriterSpi())
                writer.apply {
                    setOutput(stream)
                    val param=defaultWriteParam.apply {
                        println("Supported Compression Types:${compressionTypes!!.contentToString()}")
                        compressionMode=TIFFImageWriteParam.MODE_EXPLICIT
                        compressionType="CCITT T.6"
                    }
                    replaceImageMetadata(0,TIFFImageMetadata.parseIFD(null).asMetadata)
                    write(null, IIOImage(javaImgTmp1,null,null),param)
                    dispose()
                }
                stream.flush()
            } finally {
                stream.close()
            }
        }
    }
}

