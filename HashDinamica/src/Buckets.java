import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * @author Axell Brendow ( https://github.com/axell-brendow )
 */

public class Buckets
{
	/**
	* @author Axell Brendow ( https://github.com/axell-brendow )
	*/

   class Bucket
   {

   }
	
	RandomAccessFile arquivoDosBuckets;
	int numeroDeRegistrosPorBucket;
	
	public Buckets(String nomeDoArquivoDosBuckets, int numeroDeRegistrosPorBucket)
	{
		arquivoDosBuckets = IO.openFile(nomeDoArquivoDosBuckets, "rw");
		this.numeroDeRegistrosPorBucket = lerNumeroDeRegistrosPorBucket();
		
		if (this.numeroDeRegistrosPorBucket < 1)
		{
			this.numeroDeRegistrosPorBucket =
				escreverNumeroDeRegistrosPorBucket(numeroDeRegistrosPorBucket);
		}
	}
	
	/**
	 * Checa se o arquivo dos buckets está disponível para uso.
	 * 
	 * @return {@code true} se o arquivo dos buckets está disponível para uso.
	 * Caso contrário, {@code false}.
	 */
	
	public final boolean arquivoDisponivel()
	{
		return arquivoDosBuckets != null &&
				arquivoDosBuckets.getChannel().isOpen();
	}
	
	/**
	 * Fecha o arquivo dos buckets.
	 * 
	 * @return {@code true} se nada falhar. Caso contrário, {@code false}.
	 */
	
	public boolean close()
	{
		boolean success = false;
		
		try
		{
			arquivoDosBuckets.close();
			success = true;
		}
		
		catch (IOException ioex)
		{
			ioex.printStackTrace();
		}
		
		return success;
	}
	
	/**
	 * Lê o numero de registros por bucket do cabeçalho do arquivo dos buckets.
	 * 
	 * @return 0 se o arquivo dos buckets não estiver disponível. Caso
	 * contrário, retorna o numero de registros por bucket.
	 */
	
	private int lerNumeroDeRegistrosPorBucket()
	{
		int numeroDeRegistrosPorBucket = 0;
		
		if (arquivoDisponivel())
		{
			try
			{
				arquivoDosBuckets.seek(0);
				numeroDeRegistrosPorBucket = arquivoDosBuckets.readInt();
			}
			
			catch (IOException ioex)
			{
				ioex.printStackTrace();
			}
		}
		
		return numeroDeRegistrosPorBucket;
	}
	
	/**
	 * Escreve o numero de registros por bucket no cabeçalho do arquivo dos
	 * buckets.
	 * 
	 * @param numeroDeRegistrosPorBucket Numero de registros por bucket a ser
	 * colocado no cabeçalho do arquivo dos buckets.
	 * 
	 * @return 0 se o arquivo dos buckets não estiver disponível ou
	 * se {@code numeroDeRegistrosPorBucket} &lt= 0. Caso contrário, retorna
	 * {@code numeroDeRegistrosPorBucket}.
	 */
	
	private int escreverNumeroDeRegistrosPorBucket(int numeroDeRegistrosPorBucket)
	{
		int registrosPorBucket = 0;
		
		if (arquivoDisponivel() && numeroDeRegistrosPorBucket > 0)
		{
			registrosPorBucket = numeroDeRegistrosPorBucket;
			
			try
			{
				arquivoDosBuckets.seek(0);
				arquivoDosBuckets.writeInt(numeroDeRegistrosPorBucket);
			}
			
			catch (IOException ioex)
			{
				ioex.printStackTrace();
			}
		}
		
		return registrosPorBucket;
	}
}
