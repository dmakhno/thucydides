package net.thucydides.core.screenshots;

import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The photographer takes and stores screenshots during the test.
 * The actual screenshots are taken using the specified web driver,
 * and are stored in the specified target directory. Screenshots
 * are numbered sequentially.
 *
 * @author johnsmart
 *
 */
public class Photographer {

    private static final int MESSAGE_DIGEST_MASK = 0xFF;
    private final TakesScreenshot driver;
    private final File targetDirectory;
    private final ScreenshotSequence screenshotSequence;
    private final MessageDigest digest;

    private static final Logger LOGGER = LoggerFactory.getLogger(Photographer.class);

    private static final ScreenshotSequence DEFAULT_SCREENSHOT_SEQUENCE = new ScreenshotSequence();
    
    public Photographer(final TakesScreenshot driver, final File targetDirectory) {
        this.driver = driver;
        this.targetDirectory = targetDirectory;
        this.screenshotSequence = DEFAULT_SCREENSHOT_SEQUENCE;
        this.digest = getMd5Digest();
    }

    private MessageDigest getMd5Digest() {
        MessageDigest md = null;        
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            LOGGER.error("Failed to create digest for screenshot name.", e);
        }
        return md;
    }

    protected long nextScreenshotNumber() {
        return screenshotSequence.next();
    }
    
    private String nextScreenshotName(final String prefix) {
        long nextScreenshotNumber = nextScreenshotNumber() ;
        return "screenshot-" + getMD5DigestFrom(prefix) + nextScreenshotNumber + ".png";
    }

    private String getMD5DigestFrom(final String value) {
        byte[] messageDigest = digest.digest(value.getBytes());
        StringBuffer hexString = new StringBuffer();
        for (int i=0;i<messageDigest.length;i++) {
            hexString.append(Integer.toHexString(MESSAGE_DIGEST_MASK & messageDigest[i]));
        }
        return hexString.toString();
    }
    
    /**
     * Take a screenshot of the current browser and store it in the output directory.
     */
    public File takeScreenshot(final String prefix) throws IOException {
        File screenshot = driver.getScreenshotAs(OutputType.FILE);
        File savedScreenshot = new File(targetDirectory, nextScreenshotName(prefix));
        FileUtils.copyFile(screenshot, savedScreenshot);
        
        return savedScreenshot;
    }

}
