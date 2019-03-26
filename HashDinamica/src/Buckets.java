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
	Bucket<TIPO_DAS_CHAVES, TIPO_DOS_DADOS> bucket;
	int numeroDeRegistrosPorBucket;
	
	/**
	 * Cria um objeto que gerencia os buckets de uma hash dinâmica.
	 * 
	 * @param nomeDoArquivoDosBuckets Nome do arquivo previamente usado para os buckets.
	 * Caso o arquivo não tenha sido criado ainda, ele será criado com este nome.
	 * @param numeroDeRegistrosPorBucket Numero de registros por bucket caso o arquivo
	 * não tenha sido criado ainda.
	 * @param quantidadeMaximaDeBytesParaAChave Tamanho máximo que a chave pode gastar.
	 * @param quantidadeMaximaDeBytesParaODado Tamanho máximo que o dado pode gastar.
	 * @param construtorDaChave Construtor da chave. É necessário que a chave tenha um
	 * construtor sem parâmetros.
	 * @param construtorDoDado Construtor do dado. É necessário que o dado tenha um
	 * construtor sem parâmetros.
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
			
			this.bucket = new Bucket<>(
				PROFUNDIDADE_LOCAL_PADRAO,
				numeroDeRegistrosPorBucket,
				quantidadeMaximaDeBytesParaAChave,
				quantidadeMaximaDeBytesParaODado,
				construtorDaChave,
				construtorDoDado);
			
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
	
	/**
	 * Tenta inserir a chave e o dado no bucket.
	 * 
	 * @param chave Chave a ser inserida.
	 * @param dado Dado que corresponde à chave.
	 * @param bucket Bucket para inserção.
	 * 
	 * @return {@code -1} se algum dos parâmetros forem {@code null};
	 * {@code 0} se o bucket estiver cheio e {@code 1} se a inserção
	 * for bem sucedida.
	 */
	
	private int inserir(
		TIPO_DAS_CHAVES chave,
		TIPO_DOS_DADOS dado,
		Bucket<TIPO_DAS_CHAVES, TIPO_DOS_DADOS> bucket)
	{
		int resultado = -1;
		
		if (chave != null && dado != null && bucket != null)
		{
			resultado = ( bucket.inserir(chave, dado) ? 1 : 0 );
		}
		
		return resultado;
	}
	
	/**
	 * Tenta inserir a chave e o dado no bucket.
	 * 
	 * @param chave Chave a ser inserida.
	 * @param dado Dado que corresponde à chave.
	 * @param enderecoDoBucket Endereço do bucket no arquivo dos buckets.
	 * 
	 * @return {@code -1} se algum dos parâmetros forem {@code null}, ou
	 * o endereço do bucket for <= -1 ou o arquivo dos buckets não estiver
	 * disponível; {@code 0} se o bucket estiver cheio e {@code 1} se a
	 * inserção for bem sucedida.
	 */
	
	public int inserir(TIPO_DAS_CHAVES chave, TIPO_DOS_DADOS dado, long enderecoDoBucket)
	{
		int resultado = -1;
		
		if (enderecoDoBucket > -1 && arquivoDisponivel())
		{
			try
			{
				arquivoDosBuckets.seek(enderecoDoBucket);
				
				bucket.lerObjeto(arquivoDosBuckets);
				
				resultado = inserir(chave, dado, bucket);
			}
			
			catch (IOException ioex)
			{
				ioex.printStackTrace();
			}
		}
		
		return resultado;
	}
}
