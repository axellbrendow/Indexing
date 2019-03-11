import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * @author Axell Brendow ( https://github.com/axell-brendow )
 */

public class Diretorio
{
    RandomAccessFile arquivoDoDiretorio;
    byte profundidadeGlobal;
    
    public Diretorio(String nomeDoArquivoDoDiretorio, byte profundidadeGlobal)
    {
        arquivoDoDiretorio = IO.openFile(nomeDoArquivoDoDiretorio, "rw");
        this.profundidadeGlobal = lerProfundidadeGlobal();
        
        if (this.profundidadeGlobal < 1)
        {
            this.profundidadeGlobal =
                escreverProfundidadeGlobal(profundidadeGlobal);
        }
    }
    
    /**
     * Checa se o arquivo do diretório está disponível para uso.
     * 
     * @return {@code true} se o arquivo do diretório está disponível para uso.
     * Caso contrário, {@code false}.
     */
    
    public final boolean arquivoDisponivel()
    {
        return arquivoDoDiretorio != null &&
                arquivoDoDiretorio.getChannel().isOpen();
    }
    
    /**
     * Fecha o arquivo do diretório.
     * 
     * @return {@code true} se nada falhar. Caso contrário, {@code false}.
     */
    
    public boolean close()
    {
        boolean success = false;
        
        try
        {
            arquivoDoDiretorio.close();
            success = true;
        }
        
        catch (IOException ioex)
        {
            ioex.printStackTrace();
        }
        
        return success;
    }
    
    /**
     * Lê a profundidade global do cabeçalho do arquivo do diretório.
     * 
     * @return 0 se o arquivo dos buckets não estiver disponível. Caso
     * contrário, retorna o numero de registros por bucket.
     */
    
    private byte lerProfundidadeGlobal()
    {
        byte profundidade = 0;
        
        if (arquivoDisponivel())
        {
            try
            {
                arquivoDoDiretorio.seek(0);
                profundidade = arquivoDoDiretorio.readByte();
            }
            
            catch (IOException ioex)
            {
                ioex.printStackTrace();
            }
        }
        
        return profundidade;
    }
    
    /**
     * Escreve o numero de registros por bucket no cabeçalho do arquivo do
     * diretório.
     * 
     * @param numeroDeRegistrosPorBucket Numero de registros por bucket a ser
     * colocado no cabeçalho do arquivo dos buckets.
     * 
     * @return 0 se o arquivo dos buckets não estiver disponível ou
     * se {@code numeroDeRegistrosPorBucket} &lt= 0. Caso contrário, retorna
     * {@code numeroDeRegistrosPorBucket}.
     */
    
    private byte escreverProfundidadeGlobal(byte profundidadeGlobal)
    {
        byte profundidade = 0;
        
        if (arquivoDisponivel() && profundidadeGlobal > 0)
        {
            profundidade = profundidadeGlobal;
            
            try
            {
                arquivoDoDiretorio.seek(0);
                arquivoDoDiretorio.writeShort(profundidadeGlobal);
            }
            
            catch (IOException ioex)
            {
                ioex.printStackTrace();
            }
        }
        
        return profundidade;
    }
}
