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

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.function.Function;

import hash.serializaveis.SerializavelAbstract;
import hash.util.IO;

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
	public static final int PADRAO_NUMERO_DE_REGISTROS_POR_BUCKET = 21;
	
	protected Diretorio<TIPO_DAS_CHAVES> diretorio;
	protected Buckets<TIPO_DAS_CHAVES, TIPO_DOS_DADOS> buckets;
	
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
	 * Obtem a quantidade máxima de bytes que as chaves podem gastar de acordo
	 * com o que foi recebido no construtor.
	 * 
	 * @return a quantidade máxima de bytes que as chaves podem gastar.
	 */
	
	public short obterQuantidadeMaximaDeBytesParaAChave()
	{
		return (short) buckets.bucket.registroDoIndice.quantidadeMaximaDeBytesParaAChave;
	}
	
	/**
	 * Obtem a quantidade máxima de bytes que os dados podem gastar de acordo
	 * com o que foi recebido no construtor.
	 * 
	 * @return a quantidade máxima de bytes que os dados podem gastar.
	 */
	
	public short obterQuantidadeMaximaDeBytesParaODado()
	{
		return (short) buckets.bucket.registroDoIndice.quantidadeMaximaDeBytesParaODado;
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
		return buckets.listarDadosComAChave(chave, diretorio.obterEnderecoDoBucket(chave));
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

		long enderecoDoBucket = diretorio.obterEnderecoDoBucket(chave);
		
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

		long enderecoDoBucket = diretorio.obterEnderecoDoBucket(chave);
		
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

		long enderecoDoBucket = diretorio.obterEnderecoDoBucket(chave);
		
		if (enderecoDoBucket != -1)
		{
			sucesso = buckets.excluir(chave, enderecoDoBucket);
		}
		
		return sucesso;
	}
	
	/**
	 * Exclui todos os registros com a chave informada.
	 * 
	 * @param chave Chave a ser procurada.
	 */
	
	public boolean excluirRegistrosComAChave(TIPO_DAS_CHAVES chave)
	{
		boolean sucesso = false;

		long enderecoDoBucket = diretorio.obterEnderecoDoBucket(chave);
		
		if (enderecoDoBucket != -1)
		{
			sucesso = buckets.excluirRegistrosComAChave(chave, enderecoDoBucket);
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
		
		// esta função não chama a si própria diretamente, mas pode ser
		// chamada pela função inserir()
		// se o numero de chamadas for 2, ou seja, se esta função tiver
		// sido chamada por "ela mesma" duas vezes, há uma grande
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

			// é importante notar que a antiga profundidade local do bucket
			// é (resultado), e não 2 ^ (resultado), portanto, o nome dessa
			// variável não está correto, mas não encontrei outro melhor.
			int antigaProfundidadeLocal = (int) Math.pow(2, resultado);
			int tamanhoDoDiretorio = diretorio.obterTamanhoDoDiretorio();
			int indiceDoPonteiroParaOBucket =
				diretorio.pesquisarPeloPonteiro(enderecoDoBucket);
			
			// atualiza alguns ponteiros no diretório para o novo bucket
			for (int i = indiceDoPonteiroParaOBucket + antigaProfundidadeLocal;
					i < tamanhoDoDiretorio; i += antigaProfundidadeLocal)
			{
				diretorio.atribuirPonteiroNoIndice(i, enderecoDoNovoBucket);
			}
			
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
				dado + "\n"
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

		long enderecoDoBucket = diretorio.obterEnderecoDoBucket(chave);
		
		if (enderecoDoBucket != -1)
		{
			byte resultado = buckets.inserir(chave, dado, enderecoDoBucket);
			
			if (resultado == -1) // inserção bem sucedida
			{
				sucesso = true;
			}
			
			else if (resultado == -2) // O par (chave, dado já existe)
			{
				IO.println(
					"Inclusão ignorada. O par (chave, dado) abaixo já existe na hash.\n\n" +
					"Chave:\n" +
					chave + "\n" +
					"Dado:\n" +
					dado + "\n\n"
				);
			}
			
			// bucket cheio, "resultado" será igual à profundidade local do bucket
			else if (resultado >= 0)
			{
				tratarBucketCheio(enderecoDoBucket, resultado, chave, dado);
				
				sucesso = numeroDeChamadas < 2;
			}
		}
		
		return sucesso;
	}
	
	/**
	 * Fecha todos os arquivos que este objeto estiver gerenciando.
	 * 
	 * @return {@code true} se tudo der certo. Caso contrário, {@code false}.
	 */
	
	public boolean fechar()
	{
		return
			diretorio	!= null && diretorio.fechar() &&
			buckets		!= null && buckets	.fechar();
	}
	
	/**
	 * Cria uma representação visual da hash dinâmica mostrando
	 * o diretório e os buckets dela.
	 * 
	 * @param delimitadorEntreOsPonteirosDoDiretorio Esta será a
	 * string que irá separar cada ponteiro do diretório.
	 * @param delimitadorEntreRegistros Esta será a string que irá
	 * separar cada registro dos buckets.
	 * @param delimitadorEntreOsCamposDoRegistro Esta será a string
	 * que irá separar cada campo registro. (lápide, chave, dado).
	 * @param mostrarApenasAsChavesDosRegistros Se {@code true},
	 * mostra apenas as chaves dos registros do bucket ignorando
	 * os valores ligados às chaves e às lápides.
	 * @param mostrarDiretorio Se {@code true}, mostra o diretório
	 * da hash dinâmica.
	 * 
	 * @return uma representação visual da hash dinâmica mostrando
	 * o diretório e os buckets dela.
	 */
	
	public String toString(
		String delimitadorEntreOsPonteirosDoDiretorio,
		String delimitadorEntreRegistros,
		String delimitadorEntreOsCamposDoRegistro,
		boolean mostrarApenasAsChavesDosRegistros,
		boolean mostrarDiretorio)
	{
		return
			(
				mostrarDiretorio ? (
        			"Diretorio:\n" +
        			diretorio.toString(
        				delimitadorEntreOsPonteirosDoDiretorio) + "\n"
        		) : ""
			) +
			
			"Buckets:\n" +
			buckets.toString(
				delimitadorEntreRegistros,
				delimitadorEntreOsCamposDoRegistro,
				mostrarApenasAsChavesDosRegistros);
	}
	
	/**
	 * Cria uma representação visual da hash dinâmica mostrando
	 * o diretório e os buckets dela.
	 * 
	 * @param mostrarApenasAsChavesDosRegistros Se {@code true},
	 * mostra apenas as chaves dos registros do bucket ignorando
	 * os valores ligados às chaves e às lápides.
	 * @param mostrarDiretorio Se {@code true}, mostra o diretório
	 * da hash dinâmica.
	 * 
	 * @return uma representação visual da hash dinâmica mostrando
	 * o diretório e os buckets dela.
	 */
	
	public String toString(boolean mostrarApenasAsChavesDosRegistros, boolean mostrarDiretorio)
	{
		return toString(
			"\n", "\t, ", ", ",
			mostrarApenasAsChavesDosRegistros,
			mostrarDiretorio);
	}
	
	/**
	 * Cria uma representação visual da hash dinâmica mostrando
	 * o diretório e os buckets dela.
	 * 
	 * @param mostrarApenasAsChavesDosRegistros Se {@code true},
	 * mostra apenas as chaves dos registros do bucket ignorando
	 * os valores ligados às chaves e às lápides.
	 * 
	 * @return uma representação visual da hash dinâmica mostrando
	 * o diretório e os buckets dela.
	 */
	
	public String toString(boolean mostrarApenasAsChavesDosRegistros)
	{
		return toString(mostrarApenasAsChavesDosRegistros, true);
	}
	
	@Override
	public String toString()
	{
		return toString(false);
	}
}
