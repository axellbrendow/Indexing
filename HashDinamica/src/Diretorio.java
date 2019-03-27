import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.function.Function;

/**
 * Classe para gerenciamento do diretório de uma hash dinâmica.
 * 
 * @author Axell Brendow ( https://github.com/axell-brendow )
 *
 * @param <TIPO_DAS_CHAVES> Classe da chave.
 */

public class Diretorio<TIPO_DAS_CHAVES extends Serializavel>
{
	// bytes para a profundidade global
	private static final int DESLOCAMENTO_DO_CABECALHO = Byte.BYTES;
	// bytes para cada endereço de bucket (para cada ponteiro)
	private static final int TAMANHO_DOS_PONTEIROS = Long.BYTES;
	private static final byte PADRAO_PROFUNDIDADE_GLOBAL = 1;
	
	private RandomAccessFile arquivoDoDiretorio;
	private byte profundidadeGlobal;
	private Function<TIPO_DAS_CHAVES, Integer> funcaoHash;
	
	/**
	 * Cria um objeto que gerencia o diretório de uma hash dinâmica.
	 * 
	 * @param nomeDoArquivoDoDiretorio Nome do arquivo previamente usado para o diretório.
	 * Caso o arquivo não tenha sido criado ainda, ele será criado com este nome.
	 * @param funcaoHash Função de dispersão (hash) que será usada para as chaves. É
	 * importante ressaltar que essa função só precisa gerar valores aleatórios, não
	 * importando o tamanho dos valores.
	 */
	
	public Diretorio(String nomeDoArquivoDoDiretorio, Function<TIPO_DAS_CHAVES, Integer> funcaoHash)
	{
		arquivoDoDiretorio = IO.openFile(nomeDoArquivoDoDiretorio, "rw");
		this.profundidadeGlobal = lerProfundidadeGlobal();
		this.funcaoHash = funcaoHash;
		
		if (this.profundidadeGlobal < 1)
		{
			this.profundidadeGlobal = PADRAO_PROFUNDIDADE_GLOBAL;
		}
		
		iniciarDiretorio();
	}
	
	public byte obterProfundidadeGlobal()
	{
		return profundidadeGlobal;
	}
	
	/**
	 * Checa se o arquivo do diretório está disponível para uso.
	 * 
	 * @return {@code true} se o arquivo do diretório está disponível para uso.
	 * Caso contrário, {@code false}.
	 */
	
	private final boolean arquivoDisponivel()
	{
		return arquivoDoDiretorio != null &&
			arquivoDoDiretorio.getChannel().isOpen();
	}
	
	/**
	 * Este método escreve a profundidade global do diretório no arquivo e,
	 * em seguida, escreve o primeiro ponteiro para o primeiro bucket.
	 */
	
	private void iniciarDiretorio()
	{
		if (arquivoDisponivel())
		{
			try
			{
				arquivoDoDiretorio.seek(0);
				arquivoDoDiretorio.writeByte(profundidadeGlobal);
				// o endereço do primeiro bucket no arquivo dos buckets é 0
				arquivoDoDiretorio.writeLong(0);
			}
			
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Fecha o arquivo do diretório.
	 * 
	 * @return {@code true} se nada falhar. Caso contrário, {@code false}.
	 */
	
	public boolean fechar()
	{
		boolean sucesso = false;
		
		try
		{
			arquivoDoDiretorio.close();
			sucesso = true;
		}
		
		catch (IOException ioex)
		{
			ioex.printStackTrace();
		}
		
		return sucesso;
	}
	
	/**
	 * Lê a profundidade global do cabeçalho do arquivo do diretório.
	 * 
	 * @return 0 se o arquivo do diretório não estiver disponível. Caso
	 * contrário, retorna a profundida global do arquivo.
	 */
	
	private byte lerProfundidadeGlobal()
	{
		byte profundidade = 0;
		
		try
		{
			if (arquivoDisponivel() && arquivoDoDiretorio.length() >= DESLOCAMENTO_DO_CABECALHO)
			{
				arquivoDoDiretorio.seek(0);
				profundidade = arquivoDoDiretorio.readByte();
			}
		}
		
		catch (IOException ioex)
		{
			ioex.printStackTrace();
		}
		
		return profundidade;
	}
	
	/**
	 * Escreve a profundidade global informada no cabeçalho do arquivo do diretório.
	 * 
	 * @param profundidadeGlobal Profundidade global a ser escrita.
	 * 
	 * @return {@code profundidadeGlobal}.
	 */
	
	private byte escreverProfundidadeGlobal(byte profundidadeGlobal)
	{
		if (arquivoDisponivel() && profundidadeGlobal > 0)
		{
			try
			{
				arquivoDoDiretorio.seek(0);
				arquivoDoDiretorio.writeByte(profundidadeGlobal);
			}
			
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		
		return profundidadeGlobal;
	}
	
	/**
	 * Obtém o tamanho do diretório com base na sua profundidade global.
	 * 
	 * @return tamanho do diretório.
	 */
	
	private int obterTamanhoDoDiretorio()
	{
		return (int) Math.pow(profundidadeGlobal, 2);
	}
	
	/**
	 * Duplica o tamanho do diretório duplicando também os ponteiros.
	 */
	
	protected void duplicar()
	{
		if (arquivoDisponivel())
		{
			int tamanhoDoDiretorio = obterTamanhoDoDiretorio();
			byte[] ponteiros = new byte[tamanhoDoDiretorio * TAMANHO_DOS_PONTEIROS];
			
			try
			{
				arquivoDoDiretorio.seek(0);
				arquivoDoDiretorio.writeByte(++profundidadeGlobal);
				arquivoDoDiretorio.read(ponteiros);
				arquivoDoDiretorio.write(ponteiros);
			}
			
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Calcula o código hash de uma chave.
	 * 
	 * @param chave Chave de referência.
	 * 
	 * @return -1 se a {@code chave} ou a função hash recebida
	 * no construtor deste objeto for {@code null}. Caso contrário,
	 * retorna o código hash da {@code chave}.
	 */
	
	private int hash(TIPO_DAS_CHAVES chave)
	{
		int codigoHash = -1;
		
		if (chave != null && funcaoHash != null)
		{
			codigoHash = funcaoHash.apply(chave) % obterTamanhoDoDiretorio();
		}
		
		return codigoHash;
	}
	
	/**
	 * Obtem um ponteiro para um bucket no arquivo dos buckets.
	 * 
	 * @param indiceDoPonteiro Indice do ponteiro no diretório.
	 * 
	 * @return um ponteiro para um bucket no arquivo dos buckets.
	 */
	
	private long obterEndereco(int indiceDoPonteiro)
	{
		long endereco = -1;
		
		if (indiceDoPonteiro > -1 &&
			indiceDoPonteiro < obterTamanhoDoDiretorio() &&
			arquivoDisponivel())
		{
			try
			{
				arquivoDoDiretorio.seek(
					DESLOCAMENTO_DO_CABECALHO +
					indiceDoPonteiro * TAMANHO_DOS_PONTEIROS);
				
				endereco = arquivoDoDiretorio.readLong();
			}
			
			catch (IOException ioex)
			{
				ioex.printStackTrace();
			}
		}
		
		return endereco;
	}
	
	/**
	 * Acha o indice do ponteiro para o bucket onde a chave deve ficar.
	 * 
	 * @param chave Chave de referência.
	 * 
	 * @return o indice do ponteiro para o bucket onde a chave deve ficar.
	 */
	
	public int obterIndiceDoPonteiroParaOBucket(TIPO_DAS_CHAVES chave)
	{
		return hash(chave);
	}
	
	/**
	 * Acha o ponteiro para o bucket onde a chave deve ficar.
	 * 
	 * @param chave Chave de referência.
	 * 
	 * @return o ponteiro para o bucket onde a chave deve ficar.
	 */
	
	public long obterEndereçoDoBucket(TIPO_DAS_CHAVES chave)
	{
		long endereco = -1;

		int codigoHash = hash(chave);
		
		if (codigoHash != -1)
		{
			endereco = obterEndereco(codigoHash); 
		}
		
		return endereco;
	}
}
