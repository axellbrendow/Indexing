import java.io.File;
import hash.hashs.HashStringInt;
public class testSource
{
    public static void main(String[] args)
    {
        new File("diretorio.dir").delete();
        new File("buckets.db").delete();
        HashStringInt hash = new HashStringInt("diretorio.dir", "buckets.db", 2);
        hash.inserir("a", 1);
        hash.inserir("b", 2);
        hash.inserir("c", 3);
        System.out.println(hash);
    }
}

