/**
 * Estrutura de hashing dinâmico para indexamento de registros.
 * 
 * @author Axell Brendow ( https://github.com/axell-brendow )
 */

public class HashDinamica
{
    Diretorio diretorio;
    Buckets buckets;
    
    public HashDinamica(String nomeDoArquivoDoDiretorio, String nomeDoArquivoDosBuckets, int numeroDeRegistrosPorBucket)
    {
        diretorio = new Diretorio(nomeDoArquivoDoDiretorio, (byte) 30);
        buckets = new Buckets(nomeDoArquivoDosBuckets, numeroDeRegistrosPorBucket);
    }
}
