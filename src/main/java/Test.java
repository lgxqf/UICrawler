import org.jcodec.api.SequenceEncoder;
import org.jcodec.common.io.NIOUtils;
import org.jcodec.common.io.SeekableByteChannel;
import org.jcodec.common.model.Rational;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.*;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.*;

import org.jcodec.api.awt.AWTSequenceEncoder;

import javax.imageio.ImageIO;

public class Test {
    public static Logger log = LoggerFactory.getLogger(Test.class);

    private static class CtrlCHandler extends Thread{
        @Override
        public void run(){
            log.info("Hello!");

        }
    }

    public static void main(String args[]) throws Exception {
        List<String> fullList = Util.getFileList("/Users/justin/Project/middleground-agent/mg-uicrawler/SJE0217B29005225-2018-07-23-15_06_29/screenshot", ".png",true);

        PictureUtil.picToVideo("testing_steps1.mp4", fullList);

        //PictureUtil.rotatePicture("/Users/justin/Desktop/2018-07-18_12-06-59_X-720-Y-539-click.png");

    }

}
