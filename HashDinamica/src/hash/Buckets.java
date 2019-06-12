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

package hash;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.function.BiFunction;

import serializaveis.SerializavelAbstract;
import util.IO;

/**
 * Classe que gerencia os buckets de uma hash dinâmica.
 * 
 * @author Axell Brendow ( https://github.com/axell-brendow )
 *
 * @param <TIPO_DAS_CHAVES> Classe da chave.
 * @param <TIPO_DOS_DADOS> Classe do dado.
 */

public class Buckets<TIPO_DAS_CHAVES extends SerializavelAbstract, TIPO_DOS_DADOS extends SerializavelAbstract>
{
	/**
	 * O cabeçalho do arquivo dos buckets é a quantidade de registros por bucket (int).
	 */
	public static final int DESLOCAMENTO_CABECALHO = Integer.BYTES;
	
	protected RandomAccessFile arquivoDosBuckets;
	protected Bucket<TIPO_DAS_CHAVES, TIPO_DOS_DADOS> bucket;
	protected int numeroDeRegistrosPorBucket;
	
	private String toStr;
	
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
		
		if (arquivoDosBuckets != null)
		{
			try
			{
				arquivoDosBuckets.close();
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
	 * Reinicia um bucket excluindo todos os seus registros.
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
			bucket = bucket.criarBucket(bucket.profundidadeLocal);
			
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
	 * Procura um registro no bucket com a chave informada.
	 * 
	 * @param chave Chave a ser procurada.
	 * @param bucket Bucket para procura.
	 * 
	 * @return {@code null} se o registro não for encontrado;
	 * o dado correspondente à chave caso contrário.
	 */
	
	protected TIPO_DOS_DADOS pesquisarDadoPelaChave(
		TIPO_DAS_CHAVES chave,
		Bucket<TIPO_DAS_CHAVES, TIPO_DOS_DADOS> bucket)
	{
		TIPO_DOS_DADOS dado = null;
		
		if (bucket != null)
		{
			dado = bucket.pesquisarDadoPelaChave(chave);
		}
		
		return dado;
	}
	
	/**
	 * Procura um registro no bucket com a chave informada.
	 * 
	 * @param chave Chave a ser procurada.
	 * @param enderecoDoBucket Endereço do bucket no arquivo dos buckets.
	 * 
	 * @return {@code null} se o registro não for encontrado;
	 * o dado correspondente à chave caso contrário.
	 */
	
	public TIPO_DOS_DADOS pesquisarDadoPelaChave(TIPO_DAS_CHAVES chave, long enderecoDoBucket)
	{
		TIPO_DOS_DADOS dado = null;
		
		if (enderecoDoBucket > -1 && arquivoDisponivel())
		{
			try
			{
				arquivoDosBuckets.seek(enderecoDoBucket);
				
				bucket.lerObjeto(arquivoDosBuckets);
				
				dado = pesquisarDadoPelaChave(chave, bucket);
			}
			
			catch (IOException ioex)
			{
				ioex.printStackTrace();
			}
		}
		
		return dado;
	}
	
	/**
	 * Procura um registro no bucket com o dado informado.
	 * 
	 * @param dado Dado que corresponde à chave.
	 * @param bucket Bucket para procura.
	 * 
	 * @return {@code null} se o registro não for encontrado;
	 * a chave correspondente ao dado caso contrário.
	 */
	
	protected TIPO_DAS_CHAVES pesquisarChavePeloDado(
		TIPO_DOS_DADOS dado,
		Bucket<TIPO_DAS_CHAVES, TIPO_DOS_DADOS> bucket)
	{
		TIPO_DAS_CHAVES chave = null;
		
		if (bucket != null)
		{
			chave = bucket.pesquisarChavePeloDado(dado);
		}
		
		return chave;
	}
	
	/**
	 * Procura um registro no bucket com o dado informado.
	 * 
	 * @param dado Dado a ser procurado.
	 * @param enderecoDoBucket Endereço do bucket no arquivo dos buckets.
	 * 
	 * @return {@code null} se o registro não for encontrado;
	 * a chave correspondente ao dado caso contrário.
	 */
	
	public TIPO_DAS_CHAVES pesquisarChavePeloDado(TIPO_DOS_DADOS dado, long enderecoDoBucket)
	{
		TIPO_DAS_CHAVES chave = null;
		
		if (enderecoDoBucket > -1 && arquivoDisponivel())
		{
			try
			{
				arquivoDosBuckets.seek(enderecoDoBucket);
				
				bucket.lerObjeto(arquivoDosBuckets);
				
				chave = pesquisarChavePeloDado(dado, bucket);
			}
			
			catch (IOException ioex)
			{
				ioex.printStackTrace();
			}
		}
		
		return chave;
	}
	
	/**
	 * Tenta excluir a chave e o dado do bucket.
	 * 
	 * @param chave Chave a ser excluída.
	 * @param dado Dado que corresponde à chave.
	 * @param bucket Bucket para exclusão.
	 * 
	 * @return {@code true} se tudo der certo;
	 * {@code false} caso contrário.
	 */
	
	private boolean excluir(
		TIPO_DAS_CHAVES chave,
		TIPO_DOS_DADOS dado,
		Bucket<TIPO_DAS_CHAVES, TIPO_DOS_DADOS> bucket)
	{
		boolean resultado = false;
		
		if (chave != null && dado != null && bucket != null)
		{
			resultado = bucket.excluir(chave, dado);
		}
		
		return resultado;
	}
	
	/**
	 * Tenta excluir a chave e o dado do bucket.
	 * 
	 * @param chave Chave a ser excluída.
	 * @param dado Dado que corresponde à chave.
	 * @param enderecoDoBucket Endereço do bucket no arquivo dos buckets.
	 * 
	 * @return {@code true} se tudo der certo;
	 * {@code false} caso contrário.
	 */
	
	public boolean excluir(TIPO_DAS_CHAVES chave, TIPO_DOS_DADOS dado, long enderecoDoBucket)
	{
		boolean resultado = false;
		
		if (enderecoDoBucket > -1 && arquivoDisponivel())
		{
			try
			{
				arquivoDosBuckets.seek(enderecoDoBucket);
				
				bucket.lerObjeto(arquivoDosBuckets);
				
				resultado = excluir(chave, dado, bucket);
				
				if (resultado == true) // excluído com sucesso
				{
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
	
	/**
	 * Tenta excluir o primeiro registro com a chave informada do bucket.
	 * 
	 * @param chave Chave a ser excluída.
	 * @param bucket Bucket para exclusão.
	 * 
	 * @return {@code true} se tudo der certo;
	 * {@code false} caso contrário.
	 */
	
	private boolean excluir(
		TIPO_DAS_CHAVES chave,
		Bucket<TIPO_DAS_CHAVES, TIPO_DOS_DADOS> bucket)
	{
		boolean resultado = false;
		
		if (chave != null && bucket != null)
		{
			resultado = bucket.excluir(chave);
		}
		
		return resultado;
	}
	
	/**
	 * Lê o bucket do endereço informado.
	 *  
	 * @param enderecoDoBucket Endereço do bucket no arquivo dos buckets.
	 * 
	 * @return {@code true} se o processo for bem sucedido. Caso
	 * contrário, {@code false}.
	 */
	
	private boolean lerBucket(long enderecoDoBucket)
	{
		boolean resultado = false;
		
		if (enderecoDoBucket > -1 && arquivoDisponivel())
		{
			try
			{
				arquivoDosBuckets.seek(enderecoDoBucket);
				
				bucket.lerObjeto(arquivoDosBuckets);
				
				resultado = true;
			}
			
			catch (IOException ioex)
			{
				ioex.printStackTrace();
			}
		}
		
		return resultado;
	}
	
	/**
	 * Tenta excluir o primeiro registro com a chave informada.
	 * 
	 * @param chave Chave a ser excluída.
	 * @param enderecoDoBucket Endereço do bucket no arquivo dos buckets.
	 * 
	 * @return {@code true} se tudo der certo;
	 * {@code false} caso contrário.
	 */
	
	public boolean excluir(TIPO_DAS_CHAVES chave, long enderecoDoBucket)
	{
		boolean resultado = false;
		
		// lê o bucket no endereço informado e registra-o numa variável global
		if (lerBucket(enderecoDoBucket))
		{
			resultado = excluir(chave, bucket);
			
			if (resultado == true) // excluído com sucesso
			{
				try
				{
					arquivoDosBuckets.seek(enderecoDoBucket);
					bucket.escreverObjeto(arquivoDosBuckets);
				}
				
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		}
		
		return resultado;
	}
	
	/**
	 * Exclui todos os registros com a chave informada.
	 * 
	 * @param chave Chave a ser procurada.
	 * @param enderecoDoBucket Endereço do bucket no arquivo dos buckets.
	 */
	
	public boolean excluirRegistrosComAChave(TIPO_DAS_CHAVES chave, long enderecoDoBucket)
	{
		boolean resultado = false;
		
		// lê o bucket no endereço informado e registra-o numa variável global
		if (lerBucket(enderecoDoBucket) && chave != null)
		{
			bucket.excluirRegistrosComAChave(chave);
			
			try
			{
				arquivoDosBuckets.seek(enderecoDoBucket);
				bucket.escreverObjeto(arquivoDosBuckets);
				resultado = true;
			}
			
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		
		return resultado;
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
				
				else if (resultado > -1) // bucket cheio
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
	
	/**
	 * Percorre todos os buckets da hash aplicando um método
	 * em cada um deles. Esse método deve retornar um valor inteiro
	 * que indica se o procedimento deve parar ou não. O retorno
	 * 0 indica que o processo deve continuar, qualquer retorno
	 * diferente termina o processo. O primeiro parâmetro que o
	 * método recebe é o bucket em questão. O segundo parâmetro
	 * é o deslocamento em relação ao início do arquivo dos
	 * buckets em que o bucket está.
	 * 
	 * @param metodo Método a ser aplicado em cada bucket.
	 * 
	 * @return 0 se o {@code metodo} sempre retornar 0. Caso contrário,
	 * retorna o que o {@code metodo} retornar.
	 */
	
	public int percorrerBuckets(
		BiFunction<Bucket<TIPO_DAS_CHAVES, TIPO_DOS_DADOS>, Long, Integer> metodo)
	{
		int condicao = 0;
		
		if (metodo != null && arquivoDisponivel())
		{
			long deslocamento = DESLOCAMENTO_CABECALHO;
			int tamanhoDeUmBucket = bucket.obterTamanhoMaximoEmBytes();
			
			try
			{
				long tamanhoDoArquivoDosBuckets = arquivoDosBuckets.length();
				arquivoDosBuckets.seek(deslocamento);
				
				while (condicao == 0 && deslocamento < tamanhoDoArquivoDosBuckets)
				{
					bucket.lerObjeto(arquivoDosBuckets);
					
					condicao = metodo.apply(bucket, deslocamento);
					
					deslocamento += tamanhoDeUmBucket;
				}
			}
			
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		
		return condicao;
	}
	
	public String toString(String delimitadorEntreRegistros, String delimitadorEntreOsCamposDoRegistro, boolean mostrarApenasAsChaves)
	{
		toStr = "";
		
		percorrerBuckets(
			(bucket, deslocamento) ->
			{
				int status = 0; // indica que o processo deve continuar
				
				toStr += "Endereço: " + deslocamento + "\t" +
					"P. Local: " + bucket.profundidadeLocal + "\t" +
					bucket.toString(
					delimitadorEntreRegistros,
					delimitadorEntreOsCamposDoRegistro, 
					mostrarApenasAsChaves) + "\n";
				
				return status;
			}
		);
		
		return toStr;
	}
	
	@Override
	public String toString()
	{
		return toString(", ", ", ", false);
	}
}
