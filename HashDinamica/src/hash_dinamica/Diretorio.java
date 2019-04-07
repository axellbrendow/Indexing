/*
MIT License

Copyright (c) 2019 Axell Brendow Batista Moreira

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
*/

package hash_dinamica;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.function.Function;

import serializaveis.SerializavelAbstract;
import util.IO;

/**
 * Classe para gerenciamento do diretório de uma hash dinâmica.
 * 
 * @author Axell Brendow ( https://github.com/axell-brendow )
 *
 * @param <TIPO_DAS_CHAVES> Classe da chave.
 */

public class Diretorio<TIPO_DAS_CHAVES extends SerializavelAbstract>
{
	// bytes para a profundidade global +
	// bytes para o indice do ultimo ponteiro alterado
	private static final int DESLOCAMENTO_DO_CABECALHO = Byte.BYTES + Integer.BYTES;
	// bytes para cada endereço de bucket (para cada ponteiro)
	private static final int TAMANHO_DOS_PONTEIROS = Long.BYTES;
	private static final byte PADRAO_PROFUNDIDADE_GLOBAL = 0;
	private static final byte PADRAO_INDICE_DO_ULTIMO_PONTEIRO_ALTERADO = 0;
	
	private RandomAccessFile arquivoDoDiretorio;
	private byte profundidadeGlobal;
	private int indiceDoUltimoPonteiroAlterado;
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
	
	public int obterIndiceDoUltimoPonteiroAlterado()
	{
		return indiceDoUltimoPonteiroAlterado;
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
				arquivoDoDiretorio.writeInt(indiceDoUltimoPonteiroAlterado);
				// o endereço do primeiro bucket no arquivo dos buckets é 0
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
	 * <p>[ profundidade global (byte), indice do último ponteiro alterado (int) ]</p>
	 */
	
	private void lerCabecalho()
	{
		profundidadeGlobal = PADRAO_PROFUNDIDADE_GLOBAL;
		indiceDoUltimoPonteiroAlterado =
			PADRAO_INDICE_DO_ULTIMO_PONTEIRO_ALTERADO;
		
		try
		{
			if (arquivoDisponivel() && arquivoDoDiretorio.length() >= DESLOCAMENTO_DO_CABECALHO)
			{
				arquivoDoDiretorio.seek(0);
				profundidadeGlobal = arquivoDoDiretorio.readByte();
				indiceDoUltimoPonteiroAlterado = arquivoDoDiretorio.readInt();
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
	
	private int obterTamanhoDoDiretorio()
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
					DESLOCAMENTO_DO_CABECALHO +
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
				
				if (indice > indiceDoUltimoPonteiroAlterado)
				{
					indiceDoUltimoPonteiroAlterado = indice;
				}
				
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
				// pula o indice do último ponteiro alterado
				arquivoDoDiretorio.skipBytes(Integer.BYTES);
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
	 * Obtem um ponteiro para um bucket no arquivo dos buckets.
	 * 
	 * @param indiceDoPonteiro Indice do ponteiro no diretório.
	 * 
	 * @return um ponteiro para um bucket no arquivo dos buckets.
	 */
	
	private long obterPonteiro(int indiceDoPonteiro)
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
	
	public long obterEndereçoDoBucket(TIPO_DAS_CHAVES chave)
	{
		long ponteiro = -1;

		int codigoHash = hash(chave);
		
		if (codigoHash != -1)
		{
			ponteiro = obterPonteiro(codigoHash); 
		}
		
		return ponteiro;
	}
}
