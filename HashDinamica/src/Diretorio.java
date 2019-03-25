import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.function.Function;

/**
 * Classe para gerenciamento do diretório de uma hash dinâmica.
 * 
 * @author Axell Brendow ( https://github.com/axell-brendow )
 */

public class Diretorio<TIPO_DAS_CHAVES extends Serializavel>
{
	private final int DESLOCAMENTO_DO_CABECALHO = Byte.BYTES; // bytes para a profundidade global
	private final int TAMANHO_DOS_ENDERECOS = Long.BYTES; // bytes para cada endereço de bucket
	
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
			this.profundidadeGlobal =
				escreverProfundidadeGlobal((byte) 1);
		}
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
	 * Escreve a profundidade global no cabeçalho do arquivo do diretório. 
	 * 
	 * @param profundidadeGlobal Profundidade global a ser colocada no
	 * cabeçalho do arquivo do diretório.
	 * 
	 * @return 0 se o arquivo do diretório não estiver disponível ou
	 * se {@code profundidadeGlobal} &lt= 0. Caso contrário, retorna
	 * {@code profundidadeGlobal}.
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
				arquivoDoDiretorio.writeByte(profundidadeGlobal);
			}
			
			catch (IOException ioex)
			{
				ioex.printStackTrace();
			}
		}
		
		return profundidade;
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
	 * Obtem o ponteiro para o bucket no arquivo dos buckets.
	 * 
	 * @param indiceDoBucket Indice do bucket no diretório.
	 * 
	 * @return ponteiro para o bucket no arquivo dos buckets.
	 */
	
	private long obterEndereco(int indiceDoBucket)
	{
		long endereco = -1;
		
		if (indiceDoBucket > -1 &&
			indiceDoBucket < obterTamanhoDoDiretorio() &&
			arquivoDisponivel())
		{
			try
			{
				arquivoDoDiretorio.seek(
					DESLOCAMENTO_DO_CABECALHO +
					indiceDoBucket * TAMANHO_DOS_ENDERECOS);
				
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
