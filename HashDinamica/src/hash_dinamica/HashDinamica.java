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

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.function.Function;

import serializaveis.SerializavelAbstract;
import util.IO;

/**
 * Estrutura de hashing dinâmico para indexamento de registros.
 * 
 * @author Axell Brendow ( https://github.com/axell-brendow )
 *
 * @param <TIPO_DAS_CHAVES> Classe da chave.
 * @param <TIPO_DOS_DADOS> Classe do dado.
 */

public class HashDinamica<TIPO_DAS_CHAVES extends SerializavelAbstract, TIPO_DOS_DADOS extends SerializavelAbstract>
{
	Diretorio<TIPO_DAS_CHAVES> diretorio;
	Buckets<TIPO_DAS_CHAVES, TIPO_DOS_DADOS> buckets;
	
	// auxilia no controle de recursividade infinita
	// da função tratarBucketCheio() juntamente com a
	// função inserir()
	private boolean chamadaInterna = false;
	private int numeroDeChamadas = 0;
	
	/**
	 * Cria um objeto que gerencia uma hash dinâmica.
	 * 
	 * @param nomeDoArquivoDoDiretorio Nome do arquivo previamente usado para o diretório.
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
	 * @param funcaoHash Função de dispersão (hash) que será usada para as chaves. É
	 * importante ressaltar que essa função só precisa gerar valores dispersos, não
	 * importando o tamanho deles. Não utilize geração de números aleatórios.
	 */
	
	public HashDinamica(
		String nomeDoArquivoDoDiretorio,
		String nomeDoArquivoDosBuckets,
		int numeroDeRegistrosPorBucket,
		short quantidadeMaximaDeBytesParaAChave,
		short quantidadeMaximaDeBytesParaODado,
		Constructor<TIPO_DAS_CHAVES> construtorDaChave,
		Constructor<TIPO_DOS_DADOS> construtorDoDado,
		Function<TIPO_DAS_CHAVES, Integer> funcaoHash)
	{
		diretorio = new Diretorio<>(nomeDoArquivoDoDiretorio, funcaoHash);
		
		buckets = new Buckets<>(
			nomeDoArquivoDosBuckets,
			numeroDeRegistrosPorBucket,
			quantidadeMaximaDeBytesParaAChave,
			quantidadeMaximaDeBytesParaODado,
			construtorDaChave,
			construtorDoDado);
	}
	
	/**
	 * Procura todos os registros com uma chave específica e gera
	 * uma lista com os dados correspondentes a essas chaves.
	 * 
	 * @param chave Chave a ser procurada.
	 * 
	 * @return lista com os dados correspondentes às chaves.
	 */
	
	public ArrayList<TIPO_DOS_DADOS> listarDadosComAChave(TIPO_DAS_CHAVES chave)
	{
		return buckets.listarDadosComAChave(chave, diretorio.obterEndereçoDoBucket(chave));
	}
	
	/**
	 * Procura um registro na hash dinâmica com a chave informada.
	 * 
	 * @param chave Chave a ser procurada.
	 * 
	 * @return {@code null} se o registro não for encontrado;
	 * o dado correspondente à chave caso contrário.
	 */
	
	public TIPO_DOS_DADOS pesquisarDadoPelaChave(TIPO_DAS_CHAVES chave)
	{
		TIPO_DOS_DADOS dado = null;

		long enderecoDoBucket = diretorio.obterEndereçoDoBucket(chave);
		
		if (enderecoDoBucket != -1)
		{
			dado = buckets.pesquisarDadoPelaChave(chave, enderecoDoBucket);
		}
		
		return dado;
	}
	
	/**
	 * Exclui o registro no bucket com a chave e o dado informados.
	 * 
	 * @param chave Chave a ser excluída.
	 * @param dado Dado que corresponde à chave.
	 * 
	 * @return {@code true} se tudo der certo;
	 * {@code false} caso contrário.
	 */
	
	public boolean excluir(TIPO_DAS_CHAVES chave, TIPO_DOS_DADOS dado)
	{
		boolean sucesso = false;

		long enderecoDoBucket = diretorio.obterEndereçoDoBucket(chave);
		
		if (enderecoDoBucket != -1)
		{
			sucesso = buckets.excluir(chave, dado, enderecoDoBucket);
		}
		
		return sucesso;
	}
	
	/**
	 * Tenta excluir o primeiro registro com a chave informada.
	 * 
	 * @param chave Chave a ser excluída.
	 * 
	 * @return {@code true} se tudo der certo;
	 * {@code false} caso contrário.
	 */
	
	public boolean excluir(TIPO_DAS_CHAVES chave)
	{
		boolean sucesso = false;

		long enderecoDoBucket = diretorio.obterEndereçoDoBucket(chave);
		
		if (enderecoDoBucket != -1)
		{
			sucesso = buckets.excluir(chave, enderecoDoBucket);
		}
		
		return sucesso;
	}
	
	/**
	 * Insere todos os registros ativados de um bucket na
	 * hash dinâmica.
	 * 
	 * @param bucket Bucket com os registros a serem inseridos.
	 */
	
	public void inserirElementosDe(Bucket<TIPO_DAS_CHAVES, TIPO_DOS_DADOS> bucket)
	{
		for (int i = 0; i < buckets.numeroDeRegistrosPorBucket; i++)
		{
			RegistroDoIndice<TIPO_DAS_CHAVES, TIPO_DOS_DADOS> registro =
				bucket.obterRegistro(i);
			
			if (registro.lapide == RegistroDoIndice.REGISTRO_ATIVADO)
			{
				inserir(registro.chave, registro.dado);
			}
		}
	}
	
	/**
	 * Cuida do processo que precisa ser feito quando tenta-se
	 * inserir um registro num bucket que está cheio.
	 * 
	 * @param enderecoDoBucket Endereço do bucket que está cheio.
	 * @param resultado Resultado do método
	 * {@link Buckets#inserir(Serializavel, Serializavel, long)}.
	 * @param chave Chave do registro não inserido.
	 * @param dado Dado do registro não inserido.
	 */
	
	private void tratarBucketCheio(
		long enderecoDoBucket,
		byte resultado,
		TIPO_DAS_CHAVES chave,
		TIPO_DOS_DADOS dado)
	{
		// conta quantas vezes esta função foi chamada por uma função
		// inserir() que tenha sido chamada por esta função.
		// (desculpe-me pela recursividade, mas é isso mesmo)
		numeroDeChamadas = ( chamadaInterna ? numeroDeChamadas + 1 : 0 );
		
		// se o numero de chamadas for 2, ou seja, se esta função tiver
		// sido chamada pela própria classe duas vezes, há uma grande
		// probabilidade de o processo recursivo ser infinito, portanto,
		// a função não roda mais.
		if (numeroDeChamadas < 2)
		{
			// profundidade local do bucket igual à profundidade global do diretório
			if (resultado == diretorio.obterProfundidadeGlobal())
			{
				diretorio.duplicar();
			}
			
			Bucket<TIPO_DAS_CHAVES, TIPO_DOS_DADOS> bucketExcluido =
				buckets.resetarBucket(enderecoDoBucket);
			
			long enderecoDoNovoBucket =
				buckets.criarBucket( (byte) (resultado + 1) );
			
			diretorio.atribuirPonteiroNoIndice
			(
				diretorio.obterIndiceDoUltimoPonteiroAlterado() + 1,
				enderecoDoNovoBucket
			);
			
			chamadaInterna = true;
			inserirElementosDe(bucketExcluido);
			
			inserir(chave, dado);
			chamadaInterna = false;
		}
		
		else
		{
			IO.println(
				"Inclusão ignorada. A chave que deseja-se inserir, juntamente\n" +
				"com outras existentes, gera duplicação infinita do diretório.\n" +
				"Experimente aumentar a quantidade de registros por bucket.\n\n" +
				"Chave:\n" +
				chave + "\n" +
				"Dado:\n" +
				dado
			);
			
			numeroDeChamadas = 0;
		}
	}
	
	/**
	 * Tenta inserir a chave e o dado na hash dinâmica.
	 * 
	 * @param chave Chave a ser inserida.
	 * @param dado Dado que corresponde à chave.
	 * 
	 * @return {@code true} se a chave e o dado forem inseridos.
	 * Caso contrário, {@code false}.
	 */
	
	public boolean inserir(TIPO_DAS_CHAVES chave, TIPO_DOS_DADOS dado)
	{
		boolean sucesso = false;

		long enderecoDoBucket = diretorio.obterEndereçoDoBucket(chave);
		
		if (enderecoDoBucket != -1)
		{
			byte resultado = buckets.inserir(chave, dado, enderecoDoBucket);
			
			if (resultado == -1) // inserção bem sucedida
			{
				sucesso = true;
			}
			
			// bucket cheio, resultado será igual à profundidade local do bucket
			else if (resultado >= 0)
			{
				tratarBucketCheio(enderecoDoBucket, resultado, chave, dado);
				
				sucesso = numeroDeChamadas < 2;
			}
		}
		
		return sucesso;
	}
}
