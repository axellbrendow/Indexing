import java.io.File;

import hash.Hash;

public class Test
{
    public static void main(String[] args)
    {
        new File("diretorio.dir").delete();
        new File("buckets.db").delete();

        Hash<String, Integer> hash = new Hash<>(
                "diretorio.dir",
                "buckets.db",
                2,
                String.class,
                Integer.class);

        hash.inserir("a", 1);
        hash.inserir("b", 2);
        hash.inserir("c", 3);

        System.out.println(hash);
    }
}
