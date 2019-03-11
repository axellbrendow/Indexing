import java.io.FileNotFoundException;
import java.io.RandomAccessFile;

/**
 * @author Axell Brendow ( https://github.com/axell-brendow )
 */

public class IO
{
    public static RandomAccessFile openFile(String fileName, String mode)
    {
        RandomAccessFile file = null;
        
        try
        {
            file = new RandomAccessFile(fileName, mode);
        }
        
        catch (FileNotFoundException fnfe)
        {
            fnfe.printStackTrace();
        }
        
        return file;
    }
}
