import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Classe para gerenciamento do diretório de uma hash dinâmica.
 * 
 * @author Axell Brendow ( https://github.com/axell-brendow )
 */

public class Diretorio
{
	RandomAccessFile arquivoDoDiretorio;
	byte profundidadeGlobal;
	
	/**
	 * Cria um objeto que gerencia o diretório de uma hash dinâmica.
	 * 
	 * @param nomeDoArquivoDoDiretorio Nome do arquivo previamente usado para o diretório.
	 * Caso o arquivo não tenha sido criado ainda, ele será criado com este nome.
	 */
	
	public Diretorio(String nomeDoArquivoDoDiretorio)
	{
		arquivoDoDiretorio = IO.openFile(nomeDoArquivoDoDiretorio, "rw");
		this.profundidadeGlobal = lerProfundidadeGlobal();
		
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
