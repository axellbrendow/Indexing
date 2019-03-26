import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.reflect.Constructor;

/**
 * Classe que gerencia os buckets de uma hash dinâmica.
 * 
 * @author Axell Brendow ( https://github.com/axell-brendow )
 */

public class Buckets<TIPO_DAS_CHAVES extends Serializavel, TIPO_DOS_DADOS extends Serializavel>
{
	private static final byte PROFUNDIDADE_LOCAL_PADRAO = 1;
	
	RandomAccessFile arquivoDosBuckets;
	int numeroDeRegistrosPorBucket;
	short quantidadeMaximaDeBytesParaAChave;
	short quantidadeMaximaDeBytesParaODado;
	Constructor<TIPO_DAS_CHAVES> construtorDaChave;
	Constructor<TIPO_DOS_DADOS> construtorDoDado;
	
	/**
	 * Cria um objeto que gerencia os buckets de uma hash dinâmica.
	 * 
	 * @param nomeDoArquivoDosBuckets Nome do arquivo previamente usado para os buckets.
	 * Caso o arquivo não tenha sido criado ainda, ele será criado com este nome.
	 * @param numeroDeRegistrosPorBucket Numero de registros por bucket caso o arquivo
	 * não tenha sido criado ainda.
	 */
	
	public Buckets(
		String nomeDoArquivoDosBuckets,
		int numeroDeRegistrosPorBucket,
		short quantidadeMaximaDeBytesParaAChave,
		short quantidadeMaximaDeBytesParaODado,
		Constructor<TIPO_DAS_CHAVES> construtorDaChave,
		Constructor<TIPO_DOS_DADOS> construtorDoDado)
	{
		if (quantidadeMaximaDeBytesParaAChave > 0 &&
			quantidadeMaximaDeBytesParaODado > 0)
		{
			arquivoDosBuckets = IO.openFile(nomeDoArquivoDosBuckets, "rw");
			this.numeroDeRegistrosPorBucket = lerNumeroDeRegistrosPorBucket();
			this.quantidadeMaximaDeBytesParaAChave = quantidadeMaximaDeBytesParaAChave;
			this.quantidadeMaximaDeBytesParaODado = quantidadeMaximaDeBytesParaODado;
			this.construtorDaChave = construtorDaChave;
			this.construtorDoDado = construtorDoDado;
			
			if (this.numeroDeRegistrosPorBucket < 1)
			{
				this.numeroDeRegistrosPorBucket =
					escreverNumeroDeRegistrosPorBucket(numeroDeRegistrosPorBucket);
			}
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
	
	public boolean fechar()
	{
		boolean sucesso = false;
		
		try
		{
			arquivoDosBuckets.close();
			sucesso = true;
		}
		
		catch (IOException ioex)
		{
			ioex.printStackTrace();
		}
		
		return sucesso;
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
	
	private int inserir(TIPO_DAS_CHAVES chave, TIPO_DOS_DADOS dado, Bucket bucket)
	{
		int resultado = -1;
		
		if (chave != null && dado != null && bucket != null)
		{
			bucket.inserir()
		}
		
		return resultado;
	}
	
	public int inserir(TIPO_DAS_CHAVES chave, TIPO_DOS_DADOS dado, long enderecoDoBucket)
	{
		int resultado = -1;
		
		if (chave != null && dado != null && enderecoDoBucket > -1 && arquivoDisponivel())
		{
			try
			{
				arquivoDosBuckets.seek(enderecoDoBucket);
				
				Bucket<TIPO_DAS_CHAVES, TIPO_DOS_DADOS> bucket = new Bucket<>(
					PROFUNDIDADE_LOCAL_PADRAO,
					numeroDeRegistrosPorBucket,
					quantidadeMaximaDeBytesParaAChave,
					quantidadeMaximaDeBytesParaODado,
					construtorDaChave,
					construtorDoDado);
				
				bucket.lerObjeto(arquivoDosBuckets);
				bucket.inserir()
			}
			
			catch (IOException ioex)
			{
				ioex.printStackTrace();
			}
		}
		
		return resultado;
	}
}
