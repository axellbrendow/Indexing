import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.reflect.Constructor;
import java.util.ArrayList;

/**
 * Classe que gerencia os buckets de uma hash dinâmica.
 * 
 * @author Axell Brendow ( https://github.com/axell-brendow )
 *
 * @param <TIPO_DAS_CHAVES> Classe da chave.
 * @param <TIPO_DOS_DADOS> Classe do dado.
 */

public class Buckets<TIPO_DAS_CHAVES extends Serializavel, TIPO_DOS_DADOS extends Serializavel>
{
	// o cabeçalho do arquivo dos buckets é a quantidade de registros por bucket (int)
	public static final int DESLOCAMENTO_CABECALHO = Integer.BYTES;
	
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
			this.numeroDeRegistrosPorBucket = lerNumeroDeRegistrosPorBucket(); // tenta recuperar do arquivo
			
			if (this.numeroDeRegistrosPorBucket < 1)
			{
				if (numeroDeRegistrosPorBucket < 1)
				{
					this.numeroDeRegistrosPorBucket = Bucket.PADRAO_NUMERO_DE_REGISTROS_POR_BUCKET;
				}
				
				else
				{
					this.numeroDeRegistrosPorBucket = numeroDeRegistrosPorBucket;
				}
			}

			escreverNumeroDeRegistrosPorBucket(this.numeroDeRegistrosPorBucket);
			
			this.bucket = new Bucket<>(
				numeroDeRegistrosPorBucket,
				quantidadeMaximaDeBytesParaAChave,
				quantidadeMaximaDeBytesParaODado,
				construtorDaChave,
				construtorDoDado);
			
			// cria o primeiro bucket no arquivo
			criarBucket(Bucket.PADRAO_PROFUNDIDADE_LOCAL);
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
		
		try
		{
			if (arquivoDisponivel() && arquivoDosBuckets.length() >= Integer.BYTES)
			{
				arquivoDosBuckets.seek(0);
				numeroDeRegistrosPorBucket = arquivoDosBuckets.readInt();
			}
		}
		
		catch (IOException e)
		{
			e.printStackTrace();
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
	 * Procura todos os registros com uma chave específica e gera
	 * uma lista com os dados correspondentes a essas chaves.
	 * 
	 * @param enderecoDoBucket Endereço do bucket a ser percorrido.
	 * 
	 * @return lista com os dados correspondentes às chaves.
	 */
	
	public ArrayList<TIPO_DOS_DADOS> listarDadosComAChave(
		TIPO_DAS_CHAVES chave, long enderecoDoBucket)
	{
		ArrayList<TIPO_DOS_DADOS> dados = null;
		
		try
		{
			arquivoDosBuckets.seek(enderecoDoBucket);
			
			bucket.lerObjeto(arquivoDosBuckets);
			
			dados = bucket.listarDadosComAChave(chave);
		}
		
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		return dados;
	}
	
	/**
	 * Cria um novo bucket no arquivo dos buckets com a profundidade local
	 * informada.
	 * 
	 * @param profundidadeLocal Profundidade local do novo bucket.
	 * 
	 * @return a endereço do novo bucket no arquivo dos buckets.
	 */
	
	public long criarBucket(byte profundidadeLocal)
	{
		long enderecoDoBucket = -1;
		
		bucket = bucket.criarBucket(profundidadeLocal);
		
		try
		{
			enderecoDoBucket = arquivoDosBuckets.length();
			arquivoDosBuckets.seek(enderecoDoBucket);
			bucket.escreverObjeto(arquivoDosBuckets);
		}
		
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		return enderecoDoBucket;
	}
	
	/**
	 * Reinicia um bucket excluindo todos os seus registros e
	 * aumenta a profundidade local em uma unidade.
	 * 
	 * @param enderecoDoBucket Endereço do bucket a ser reiniciado.
	 * 
	 * @return {@code null} se houver algum problema na leitura
	 * do bucket no arquivo. Caso contrário, o objeto {@link Bucket}
	 * que representa fielmente o bucket antes de ser reiniciado.
	 */
	
	public Bucket<TIPO_DAS_CHAVES, TIPO_DOS_DADOS> resetarBucket(long enderecoDoBucket)
	{
		Bucket<TIPO_DAS_CHAVES, TIPO_DOS_DADOS> bucketExcluido = null;
		
		try
		{
			arquivoDosBuckets.seek(enderecoDoBucket);
			bucket.lerObjeto(arquivoDosBuckets);
			
			bucketExcluido = bucket.clone();
			bucket = bucket.criarBucket((byte) (bucket.profundidadeLocal + 1));
			
			arquivoDosBuckets.seek(enderecoDoBucket);
			bucket.escreverObjeto(arquivoDosBuckets);
		}
		
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		return bucketExcluido;
	}
	
	/**
	 * Tenta inserir a chave e o dado no bucket.
	 * 
	 * @param chave Chave a ser inserida.
	 * @param dado Dado que corresponde à chave.
	 * @param bucket Bucket para inserção.
	 * 
	 * @return a profundidade local do bucket se
	 * ele estiver cheio;
	 * {@code -1} se tudo correr bem;
	 * {@code -2} se o par (chave, dado) já existe;
	 * {@code -3} se algum dos parâmetros forem {@code null}.
	 */
	
	private byte inserir(
		TIPO_DAS_CHAVES chave,
		TIPO_DOS_DADOS dado,
		Bucket<TIPO_DAS_CHAVES, TIPO_DOS_DADOS> bucket)
	{
		byte resultado = -3;
		
		if (chave != null && dado != null && bucket != null)
		{
			resultado = bucket.inserir(chave, dado);
		}
		
		return resultado;
	}
	
	/**
	 * Tenta inserir a chave e o dado no bucket. Caso o bucket esteja
	 * cheio, este método aumenta automaticamente a profundidade local
	 * dele em uma unidade.
	 * 
	 * @param chave Chave a ser inserida.
	 * @param dado Dado que corresponde à chave.
	 * @param enderecoDoBucket Endereço do bucket no arquivo dos buckets.
	 * 
	 * @return a profundidade local do bucket se
	 * ele estiver cheio;
	 * {@code -1} se tudo correr bem;
	 * {@code -2} se o par (chave, dado) já existe;
	 * {@code -3} se algum dos parâmetros forem {@code null}.
	 * {@code -4} se o endereço do bucket for <= -1 ou se o
	 * arquivo dos buckets não estiver disponível.
	 */
	
	public byte inserir(TIPO_DAS_CHAVES chave, TIPO_DOS_DADOS dado, long enderecoDoBucket)
	{
		byte resultado = -4;
		
		if (enderecoDoBucket > -1 && arquivoDisponivel())
		{
			try
			{
				arquivoDosBuckets.seek(enderecoDoBucket);
				
				bucket.lerObjeto(arquivoDosBuckets);
				
				resultado = inserir(chave, dado, bucket);
				
				if (resultado == -1) // inserido com sucesso
				{
					arquivoDosBuckets.seek(enderecoDoBucket);
					bucket.escreverObjeto(arquivoDosBuckets);
				}
				
				else if (resultado > 0) // bucket cheio
				{
					// aumenta a profundidade local
					bucket.atribuirProfundidadeLocal( (byte) (resultado + 1) );
					arquivoDosBuckets.seek(enderecoDoBucket);
					bucket.escreverObjeto(arquivoDosBuckets);
				}
			}
			
			catch (IOException ioex)
			{
				ioex.printStackTrace();
			}
		}
		
		return resultado;
	}
}
