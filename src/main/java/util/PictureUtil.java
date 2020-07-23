package util;


import org.jcodec.api.awt.AWTSequenceEncoder;
import org.jcodec.common.io.NIOUtils;
import org.jcodec.common.io.SeekableByteChannel;
import org.jcodec.common.model.Rational;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

class PictureRunnable implements Runnable{
    private int mX;
    private int mY;
    private List<Point> list;
    private String gesture;

    PictureRunnable(int x, int y){
        mX = x;
        mY = y;
    }

    PictureRunnable(List<Point> pointList, String ext){
        list = pointList;
        gesture = ext;
    }

    public void run(){
        if(list !=null){
            PictureUtil.takeAndModifyScreenShot(list,gesture);
        }else{
            PictureUtil.takeAndModifyScreenShot(mX,mY);
        }
    }
}

public class PictureUtil {
    public static Logger log = LoggerFactory.getLogger(PictureUtil.class);
    private static final int  DEFAULT_RADIUS = 20;

    private Font font = new Font("", Font.PLAIN, 20);// 添加字体的属性设置
    private int fontSize;
    private int x;
    private int y;
    private String imgSrcPath;
    private String imgDesPath;
    private String content;
    private String fontStyle;
    private Color color;

    static void takeAndModifyScreenShotAsyn(int x, int y){
        new  Thread(new PictureRunnable(x,y)).start();
    }

    static void takeAndModifyScreenShotAsyn(List<Point> list, String ext){
        new Thread(new PictureRunnable(list,ext)).start();
    }

    static String takeAndModifyScreenShot(List<Point> list,String ext){
        return takeAndModifyScreenShot(list,DEFAULT_RADIUS,ext);
    }

    static String takeAndModifyScreenShot(int x, int y){
        return takeAndModifyScreenShot(x,y,DEFAULT_RADIUS);
    }

    static String takeAndModifyScreenShot(int x, int y,int radius){

        return takeAndModifyScreenShot(x,y,radius,"click");
    }

    static String takeAndModifyScreenShot(int x, int y,int radius,String ext){
        List<Point> list = new ArrayList<>();
        list.add(new Point(x,y));
        return takeAndModifyScreenShot(list,radius,ext);
    }

    static String takeAndModifyScreenShot(List<Point> pointList,int radius,String ext){

        String img = Driver.takeScreenShot();
        drawPoint(img,pointList,radius,Color.RED);
        File file = new File(img);
        if(pointList.size() > 1) {
            img = img.replace(".png", "_" + ext + ".png");
        }else{
            Point point = pointList.get(0);
            img = img.replace(".png", "_X-" + point.x + "-Y-" + point.y + "-" + ext + ".png");
        }
        File newFile = new File(img);
        file.renameTo(newFile);
        log.info("Screen shot is renamed to: " + newFile.getAbsolutePath());
        return img;
    }

    private static void drawPoint(String srcImagePath,List<Point> pointList,int radius,Color color){
        FileOutputStream fos = null;

        try {
            //获取源图片
            BufferedImage image = ImageIO.read(new File(srcImagePath));

            //根据xy点坐标绘制连接线
            Graphics2D g2d = image.createGraphics();
            AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f);
            g2d.setComposite(ac);
            int rb = 2* radius;

            for(Point point : pointList) {
                g2d.setColor(Color.BLACK);
                g2d.fillOval(point.x - radius, point.y - radius, 2*radius, 2*radius);

                g2d.setColor(color);
                g2d.fillOval(point.x - rb, point.y - rb, 2*rb, 2*rb);            //填充一个椭圆形
            }

            fos = new FileOutputStream(srcImagePath);
            ImageIO.write(image, "png", fos);
        } catch (IOException e){
            log.error("Fail to draw in screenshot " + srcImagePath);
            e.printStackTrace();
        }finally{
            if(fos!=null){
                try {
                    fos.close();
                }catch (Exception e){
                    log.error("fail to close picture " + srcImagePath);
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 导入本地图片到缓冲区
     */
    private BufferedImage loadImageLocal(String imgSrcPath) {
        try {
            this.imgSrcPath = imgSrcPath;
            return ImageIO.read(new File(this.imgSrcPath));
        } catch (IOException e) {
            log.info(e.getMessage());
        }
        return null;
    }

    /**
     * 生成新图片到本地
     */
    private void writeImageLocal(String imgDesPath, BufferedImage img) {
        if (imgDesPath != null && img != null) {
            try {
                this.imgDesPath = imgDesPath;
                File outputFile = new File(this.imgDesPath);
                ImageIO.write(img, "jpg", outputFile);
            } catch (IOException e) {
                log.info(e.getMessage());
            }
        }
    }

    /**
     * 设定文字的字体等
     */
    private void setFont(String fontStyle, int fontSize) {
        this.fontSize = fontSize;
        this.fontStyle = fontStyle;
        this.font = new Font(this.fontStyle, Font.PLAIN, this.fontSize);
    }

    /**
     * 设定文字在图片中的位置以及文字的颜色
     */
    private void setLocalColor(int x,int y,Color color){
        this.x = x;
        this.y = y;
        this.color=color;
    }

    /**
     * 修改图片,返回修改后的图片缓冲区（只输出一行文本）
     */
    private BufferedImage modifyImage(BufferedImage img, String content) {
        Graphics2D g = null;

        try {
            int w = img.getWidth();
            int h = img.getHeight();
            g = img.createGraphics();
            //g.setBackground(Color.WHITE);
            g.setColor(this.color);
            if (this.font != null)
                g.setFont(this.font);
            // 验证输出位置的纵坐标和横坐标
            if (x >= h || y >= w) {
                this.x = h - this.fontSize + 2;
                this.y = w;
            }
            this.content=content;
            if (content != null) {
                g.drawString(this.content, this.x, this.y);
            }
            g.dispose();
        } catch (Exception e) {
            log.info(e.getMessage());
        }
        return img;
    }

    public PictureUtil(String imgSrcPath, String imgDesPath, String content,
                       String fontStyle, int fontsize, int x, int y, Color color) {
        super();
        this.fontSize = fontsize;
        this.x = x;
        this.y = y;
        this.imgSrcPath = imgSrcPath;
        this.imgDesPath = imgDesPath;
        this.content = content;
        this.fontStyle = fontStyle;
        this.color=color;
        setFont(fontStyle, fontsize);
        writeImageLocal(imgDesPath, modifyImage(loadImageLocal(imgSrcPath),
                content));
    }

    public PictureUtil(String fontStyle, int fontSize, int x, int y, Color color) {
        super();
        this.fontSize = fontSize;
        this.x = x;
        this.y = y;
        this.fontStyle = fontStyle;
        this.color=color;
        setFont(fontStyle, fontSize);
        setLocalColor(x,y,color);
    }



    public static void picToVideo(String fileName, List<String> list){
        SeekableByteChannel out = null;
        AWTSequenceEncoder encoder = null;

        int size = list.size();
        int index = 0;

        try {
            out = NIOUtils.writableFileChannel(fileName);
            encoder = new AWTSequenceEncoder(out, Rational.R(1, 1));

            for(String file : list) {
                index ++;
                BufferedImage image = ImageIO.read(new File(file));
                image = rotateImage(image);
                encoder.encodeImage(image);

                if((index % 5) == 0){
                    log.info("Video generation complete " + (index*100/size) + "%");
                }
            }
        }catch (Exception e){
            log.error("Fail to generate video!");
            e.printStackTrace();
        } finally {
            try {
                encoder.finish();
            }catch (Exception e){
                e.printStackTrace();
            }

            NIOUtils.closeQuietly(out);
        }
    }

    private static BufferedImage rotateImage(final BufferedImage oldBufferedImage) {
        int w = oldBufferedImage.getWidth();
        int h = oldBufferedImage.getHeight();
        int degree = 90;

        boolean isVertical = ConfigUtil.isVideoVertical();

        //竖屏
        if(isVertical && w < h ){
            //log.info("vertical");
            return oldBufferedImage;
        }

        //横屏
        if(!isVertical && w > h){
            //log.info("Horizontal");
            return oldBufferedImage;
        }

        if(!isVertical){
            degree = 270;
        }

        log.info("Rotate image : degree " + degree +" w " + w + " h " + h + " vertical video "+ ConfigUtil.isVideoVertical());

        int type = oldBufferedImage.getColorModel().getTransparency();

        BufferedImage img;
        Graphics2D graphics2d;

        graphics2d = (img = new BufferedImage(h, w, type))
                .createGraphics();

        graphics2d.setRenderingHint(
                RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        if(h > w){
            h = w;
        }

        graphics2d.rotate(Math.toRadians(degree), h / 2f, h / 2f);
        graphics2d.drawImage(oldBufferedImage, 0, 0,null);
        graphics2d.dispose();

        return img;
    }


    public static synchronized void jpgToGif(String pic[], String newPic, int delay) {
        try {
            AnimatedGifEncoder e = new AnimatedGifEncoder();
            e.setRepeat(0);
            e.start(newPic);
            BufferedImage src[] = new BufferedImage[pic.length];
            for (int i = 0; i < src.length; i++) {
                e.setDelay(delay); //设置播放的延迟时间
                src[i] = ImageIO.read(new File(pic[i])); // 读入需要播放的jpg文件
                e.addFrame(src[i]);  //添加到帧中
            }
            e.finish();
        } catch (Exception e) {
            System.out.println( "jpgToGif Failed:");
            e.printStackTrace();
        }
    }


    public static BufferedImage convertToType(BufferedImage sourceImage, int targetType){
        BufferedImage image;

        // if the source image is already the target type, return the source image
        if (sourceImage.getType() == targetType) {
            image = sourceImage;
        }
        else {
            // otherwise create a new image of the target type and draw the new image
            image = new BufferedImage(sourceImage.getWidth(),
                    sourceImage.getHeight(), targetType);
            image.getGraphics().drawImage(sourceImage, 0, 0, null);
        }

        return image;
    }

}



//       public static void picToVideo(String filename, List<String> fileList,int width, int height) throws Exception {
//        int picPerSecond = 1;
//        final Rational frameRate = Rational.make(1, picPerSecond);
//
//        /** First we create a muxer using the passed in filename and formatname if given. */
//        final Muxer muxer = Muxer.make(filename, null, null);
//
//        /** Now, we need to decide what type of codec to use to encode video. Muxers
//         * have limited sets of codecs they can use. We're going to pick the first one that
//         * works, or if the user supplied a codec name, we're going to force-fit that
//         * in instead.
//         */
//        final MuxerFormat format = muxer.getFormat();
//        final Codec codec = Codec.findEncodingCodec(format.getDefaultVideoCodecId());;
//
//        /**
//         * Now that we know what codec, we need to create an encoder
//         */
//        Encoder encoder = Encoder.make(codec);
//
//        /**
//         * Video encoders need to know at a minimum:
//         *   width
//         *   height
//         *   pixel format
//         * Some also need to know frame-rate (older codecs that had a fixed rate at which video files could
//         * be written needed this). There are many other options you can set on an encoder, but we're
//         * going to keep it simpler here.
//         */
//
//        encoder.setWidth(width);
//        encoder.setHeight(height);
//        // We are going to use 420P as the format because that's what most video formats these days use
//        final PixelFormat.Type pixelFormat = PixelFormat.Type.PIX_FMT_YUV420P;
//        encoder.setPixelFormat(pixelFormat);
//        encoder.setTimeBase(frameRate);
//
//        /** An annoynace of some formats is that they need global (rather than per-stream) headers,
//         * and in that case you have to tell the encoder. And since Encoders are decoupled from
//         * Muxers, there is no easy way to know this beyond
//         */
//        if (format.getFlag(MuxerFormat.Flag.GLOBAL_HEADER))
//            encoder.setFlag(Encoder.Flag.FLAG_GLOBAL_HEADER, true);
//
//        /** Open the encoder. */
//        encoder.open(null, null);
//
//        /** Add this stream to the muxer. */
//        muxer.addNewStream(encoder);
//
//        /** And open the muxer for business. */
//        muxer.open(null, null);
//
//        /** Next, we need to make sure we have the right MediaPicture format objects
//         * to encode data with. Java (and most on-screen graphics programs) use some
//         * variant of Red-Green-Blue image encoding (a.k.a. RGB or BGR). Most video
//         * codecs use some variant of YCrCb formatting. So we're going to have to
//         * convert. To do that, we'll introduce a MediaPictureConverter object later. object.
//         */
//        MediaPictureConverter converter = null;
//        final MediaPicture picture = MediaPicture.make(
//                encoder.getWidth(),
//                encoder.getHeight(),
//                pixelFormat);
//        picture.setTimeBase(frameRate);
//
//        /** Now begin our main loop of taking screen snaps.
//         * We're going to encode and then write out any resulting packets. */
//        final MediaPacket packet = MediaPacket.make();
//        int size = fileList.size();
//
//        //for (int i = 0; i < duration / framerate.getDouble(); i++) {
//        for (int i = 0; i < size; i++) {
//            /** Make the screen capture && convert image to TYPE_3BYTE_BGR */
//            BufferedImage img = ImageIO.read(new File(fileList.get(i)));
//            final BufferedImage screen = convertToType(img, BufferedImage.TYPE_3BYTE_BGR);
//            /** This is LIKELY not in YUV420P format, so we're going to convert it using some handy utilities. */
//            if (converter == null)
//                converter = MediaPictureConverterFactory.createConverter(screen, picture);
//            converter.toPicture(picture, screen, i);
//
//            do {
//                encoder.encode(packet, picture);
//                if (packet.isComplete())
//                    muxer.write(packet, false);
//            } while (packet.isComplete());
//
//            /** now we'll sleep until it's time to take the next snapshot. */
//            //Thread.sleep((long) (500 * framerate.getDouble()));
//        }
//
//        /** Encoders, like decoders, sometimes cache pictures so it can do the right key-frame optimizations.
//         * So, they need to be flushed as well. As with the decoders, the convention is to pass in a null
//         * input until the output is not complete.
//         */
//        do {
//            encoder.encode(packet, null);
//            if (packet.isComplete())
//                muxer.write(packet,  false);
//        } while (packet.isComplete());
//
//        /** Finally, let's clean up after ourselves. */
//        muxer.close();
//    }



