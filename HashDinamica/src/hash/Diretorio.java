package hash;/* See the project's root for license information. */

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Classe para gerenciamento do diretório de uma hash dinâmica.
 * 
 * @author Axell Brendow ( https://github.com/axell-brendow )
 *
 * @param <TIPO_DAS_CHAVES> Classe da chave. Caso essa classe não seja de um
 * tipo primitivo, ela deve ser filha da interface {@link Serializavel}.
 */

public class Diretorio<TIPO_DAS_CHAVES>
{
	// bytes para a profundidade global
	private static final int DESLOCAMENTO_CABECALHO = Byte.BYTES;
	// bytes para cada endereço de bucket (para cada ponteiro)
	private static final int TAMANHO_DOS_PONTEIROS = Long.BYTES;
	private static final byte PADRAO_PROFUNDIDADE_GLOBAL = 0;
	
	private RandomAccessFile arquivoDoDiretorio;
	private byte profundidadeGlobal;
	private Function<TIPO_DAS_CHAVES, Integer> funcaoHash;
	
	/**
	 * Cria um objeto que gerencia o diretório de uma hash dinâmica.
	 * 
	 * @param nomeDoArquivoDoDiretorio Nome do arquivo previamente usado para o diretório.
	 * Caso o arquivo não tenha sido criado ainda, ele será criado com este nome.
	 * @param funcaoHash Função de dispersão (hash) que será usada para as chaves. É
	 * importante ressaltar que essa função só precisa gerar valores dispersos, não
	 * importando o tamanho deles. Não utilize geração de números aleatórios.
	 */
	
	public Diretorio(String nomeDoArquivoDoDiretorio, Function<TIPO_DAS_CHAVES, Integer> funcaoHash)
	{
		arquivoDoDiretorio = IO.openFile(nomeDoArquivoDoDiretorio, "rw");
		this.funcaoHash = funcaoHash;
		lerCabecalho();
		
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
				// o endereço do primeiro bucket no arquivo dos buckets é o
				// tamanho do cabeçalho do arquivo dos buckets
				arquivoDoDiretorio.writeLong(Buckets.DESLOCAMENTO_CABECALHO);
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
		
		if (arquivoDoDiretorio != null)
		{
			try
			{
				arquivoDoDiretorio.close();
				sucesso = true;
			}
			
			catch (IOException ioex)
			{
				ioex.printStackTrace();
			}
		}
		
		return sucesso;
	}
	
	/**
	 * Lê os metadados/cabeçalho do diretório. A estrutura do cabeçalho
	 * é a seguinte:
	 * 
	 * <p>[ profundidade global (byte) ]</p>
	 */
	
	private void lerCabecalho()
	{
		profundidadeGlobal = PADRAO_PROFUNDIDADE_GLOBAL;
		
		try
		{
			if (arquivoDisponivel() && arquivoDoDiretorio.length() >= DESLOCAMENTO_CABECALHO)
			{
				arquivoDoDiretorio.seek(0);
				profundidadeGlobal = arquivoDoDiretorio.readByte();
			}
		}
		
		catch (IOException ioex)
		{
			ioex.printStackTrace();
		}
	}
	
	/**
	 * Obtém o tamanho do diretório com base na sua profundidade global.
	 * 
	 * @return tamanho do diretório.
	 */
	
	protected int obterTamanhoDoDiretorio()
	{
		return (int) Math.pow(2, profundidadeGlobal);
	}
	
	/**
	 * Faz um <i>seek</i> no arquivo do diretório até chegar em cima
	 * do ponteiro desejado.
	 *  
	 * @param indice Indice do ponteiro no diretório.
	 * 
	 * @return {@code true} se tudo der certo; {@code false} caso
	 * contrário.
	 */
	
	private boolean irParaOPonteiroNoIndice(int indice)
	{
		boolean sucesso = false;
		
		if (indice > -1 &&
			indice < obterTamanhoDoDiretorio() &&
			arquivoDisponivel())
		{
			try
			{
				arquivoDoDiretorio.seek(
					DESLOCAMENTO_CABECALHO +
					indice * TAMANHO_DOS_PONTEIROS);
				
				sucesso = true;
			}
			
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		
		return sucesso;
	}
	
	/**
	 * Muda o ponteiro no indice informado para um novo ponteiro.
	 * 
	 * @param indice Indice do ponteiro a ser alterado.
	 * @param novoPonteiro Novo valor a ser colocado.
	 * 
	 * @return {@code true} se tudo der certo; {@code false} caso
	 * contrário.
	 */
	
	protected boolean atribuirPonteiroNoIndice(int indice, long novoPonteiro)
	{
		boolean sucesso = false;
		
		if (irParaOPonteiroNoIndice(indice))
		{
			try
			{
				arquivoDoDiretorio.writeLong(novoPonteiro);
				
				sucesso = true;
			}
			
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		
		return sucesso;
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
	 * no construtor deste objeto forem {@code null}. Caso contrário,
	 * retorna o código hash da {@code chave}, que é equivalente
	 * ao indice do ponteiro para o bucket onde a chave se encaixa.
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
	 * Calcula o índice do ponteiro para o bucket da chave.
	 * 
	 * @param chave Chave de referência.
	 * 
	 * @return -1 caso ocorra algum problema. Caso contrário,
	 * retorna o índice do ponteiro para o bucket da chave.
	 */
	
	protected int obterIndiceDoPonteiroDaChave(TIPO_DAS_CHAVES chave)
	{
		return hash(chave);
	}
	
	/**
	 * Obtem um ponteiro para um bucket no arquivo dos buckets.
	 * 
	 * @param indiceDoPonteiro Indice do ponteiro no diretório.
	 * 
	 * @return um ponteiro para um bucket no arquivo dos buckets.
	 */
	
	protected long obterPonteiro(int indiceDoPonteiro)
	{
		long ponteiro = -1;
		
		if (irParaOPonteiroNoIndice(indiceDoPonteiro))
		{
			try
			{
				ponteiro = arquivoDoDiretorio.readLong();
			}
			
			catch (IOException ioex)
			{
				ioex.printStackTrace();
			}
		}
		
		return ponteiro;
	}
	
	/**
	 * Acha o ponteiro para o bucket onde a chave deve ficar.
	 * 
	 * @param chave Chave de referência.
	 * 
	 * @return o ponteiro para o bucket onde a chave deve ficar.
	 */
	
	public long obterEnderecoDoBucket(TIPO_DAS_CHAVES chave)
	{
		long ponteiro = -1;

		int codigoHash = hash(chave);
		
		if (codigoHash > -1)
		{
			ponteiro = obterPonteiro(codigoHash); 
		}
		
		return ponteiro;
	}
	
	/**
	 * Percorre todos os ponteiros do diretório aplicando um método
	 * em cada um deles. Esse método deve retornar um valor inteiro
	 * que indica se o procedimento deve parar ou não. O retorno
	 * -1 indica que o processo deve continuar, qualquer retorno
	 * diferente termina o processo. O primeiro parâmetro que o
	 * método recebe é o ponteiro em questão. O segundo parâmetro
	 * que o método recebe é o índice do ponteiro no diretório.
	 * 
	 * @param metodo Método a ser aplicado em cada ponteiro.
	 * 
	 * @return -1 se o {@code metodo} sempre retornar -1. Caso contrário,
	 * retorna o que o {@code metodo} retornar.
	 */
	
	public int percorrerPonteiros(
		BiFunction<Long, Integer, Integer> metodo)
	{
		int condicao = -1;
		
		if (metodo != null && arquivoDisponivel())
		{
			long deslocamento = DESLOCAMENTO_CABECALHO;
			
			try
			{
				long tamanhoDoArquivoDoDiretorio = arquivoDoDiretorio.length();
				long ponteiro;
				arquivoDoDiretorio.seek(deslocamento);
				
				for (int i = 0;
					condicao == -1 && deslocamento < tamanhoDoArquivoDoDiretorio;
					i++)
				{
					ponteiro = arquivoDoDiretorio.readLong();
					
					condicao = metodo.apply(ponteiro, i);
					
					deslocamento += TAMANHO_DOS_PONTEIROS;
				}
			}
			
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		
		return condicao;
	}
	
	/**
	 * Encontra o índice do primeiro ponteiro no diretório
	 * que for igual ao valor informado.
	 * 
	 * @param ponteiro Valor do ponteiro.
	 * 
	 * @return -1 se o ponteiro não for encontrado. Caso
	 * contrário, o índice do ponteiro.
	 */
	
	public int pesquisarPeloPonteiro(long ponteiro)
	{
		return
		percorrerPonteiros
		(
			(pointer, index) ->
			{
				int status = -1;
				
				if (pointer == ponteiro)
				{
					status = index;
				}
				
				return status;
			}
		);
	}
	
	public String toString(String delimitadorEntreOsPonteiros)
	{
		String str = "Arquivo do diretório não disponível.";
		
		if (arquivoDisponivel())
		{
			str = "";
			
			try
			{
				long tamanhoDoArquivoDoDiretorio = arquivoDoDiretorio.length();
				long deslocamento = DESLOCAMENTO_CABECALHO;
				long ponteiro;
				
				arquivoDoDiretorio.seek(deslocamento);
				
				for (int i = 0; deslocamento < tamanhoDoArquivoDoDiretorio; i++)
				{
					ponteiro = arquivoDoDiretorio.readLong();
					
					str += "{ " + i + "\t, " + ponteiro + "\t}" + delimitadorEntreOsPonteiros;
					
					deslocamento += TAMANHO_DOS_PONTEIROS;
				}
			}
			
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		
		return str;
	}
	
	public String toString()
	{
		return toString("\n");
	}
}
