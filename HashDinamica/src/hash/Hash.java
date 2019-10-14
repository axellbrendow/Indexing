package hash;/* See the project's root for license information. */

import java.util.ArrayList;
import java.util.function.Function;

/**
 * Estrutura de hashing dinâmico para indexamento de registros.
 * 
 * @author Axell Brendow ( https://github.com/axell-brendow )
 *
 * @param <TIPO_DAS_CHAVES> Classe da chave. Caso essa classe não seja de um
 * tipo primitivo, ela deve ser filha da interface {@link Serializavel}.
 * @param <TIPO_DOS_DADOS> Classe do dado. Caso essa classe não seja de um
 * tipo primitivo, ela deve ser filha da interface {@link Serializavel}.
 */

public class Hash<TIPO_DAS_CHAVES, TIPO_DOS_DADOS>
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
	 * Gera automaticamente uma função hash caso a classe da chave seja de um
	 * tipo primitivo.
	 *
	 * @param classeDaChave Classe da chave.
	 * @param funcaoHash Função hash já existente ou {@code null}.
	 *
	 * @return caso a função hash recebida não seja nula, retorna ela própria.
	 * Caso contrário, se a classe da chave não for de um tipo primitivo, retorna
	 * {@code null}. Se for de um tipo primitivo diferente de VOID, retorna uma
	 * função hash gerada automaticamente. <b>CUIDADO:</b> caso a classe da chave
	 * seja do tipo Float ou Double, a função hash gerada irá criar valores
	 * inteiros retirando as casas fracionárias dos valores Double ou Float. Ou
	 * seja, as chaves 1.1 e 1.2 geraram o mesmo número hash.
	 */
	private Function<TIPO_DAS_CHAVES, Integer> obterFuncaoHash(
		Class<TIPO_DAS_CHAVES> classeDaChave,
		Function<TIPO_DAS_CHAVES, Integer> funcaoHash)
	{
		if (funcaoHash == null)
		{
			if (classeDaChave.equals(String.class))
				funcaoHash = (chave) ->
				{
					int strValue = -1;

					if (chave != null && chave != null)
					{
						String str = (String) chave;
						int length = str.length();
						strValue = 0;

						// ideia extraída de:
						// https://www.ime.usp.br/~pf/algoritmos/aulas/hash.html
						for (int i = 0; i < length; i++)
						{
							strValue = strValue * 31 + str.charAt(i);
						}

						if (strValue < 0)
						{
							strValue += Integer.MAX_VALUE;
						}
					}

					return strValue;
				};

			else if (Classes.isWrapper(classeDaChave))
			{
				if (classeDaChave.equals(Boolean.class))
					funcaoHash = (chave) -> ((Boolean) chave) ? 1 : 0;

				else if (classeDaChave.equals(Character.class))
					funcaoHash = (chave) -> (int) ((Character) chave);

				else if (classeDaChave.equals(Byte.class))
					funcaoHash = (chave) -> ((Byte) chave).intValue();

				else if (classeDaChave.equals(Short.class))
					funcaoHash = (chave) -> ((Short) chave).intValue();

				else if (classeDaChave.equals(Integer.class))
					funcaoHash = (chave) -> ((Integer) chave);

				else if (classeDaChave.equals(Long.class))
					funcaoHash = (chave) -> ((Long) chave).intValue();

				else if (classeDaChave.equals(Float.class))
					funcaoHash = (chave) -> ((Float) chave).intValue();

				else if (classeDaChave.equals(Double.class))
					funcaoHash = (chave) -> ((Double) chave).intValue();

				else if (classeDaChave.equals(Void.class))
					IO.printlnerr("ERRO: a classe " + classeDaChave.getName() + " é do tipo VOID.");

				else
					IO.printlnerr("ERRO: a classe " + classeDaChave.getName() + " é inválida.");
			}

			// Checa se a classe é Serializavel
			else if (Serializavel.class.isAssignableFrom(classeDaChave))
			{
				IO.printlnerr("ERRO: a classe " + classeDaChave.getName() +
					" é serializável e a Hash não pode inferir como fazer o hash" +
					" de objetos desse tipo. Você precisa passar uma função hash" +
					" no construtor dessa classe que gere um número inteiro" +
					" não negativo a partir do seu objeto serializável.");
			}

			else IO.printlnerr("ERRO: a classe " + classeDaChave.getName() +
					" não é de tipo primitivo nem implementa a interface Serializavel.");
		}

		return funcaoHash;
	}
	
	/**
	 * Cria uma Hash Extensível.
	 * 
	 * @param nomeDoArquivoDoDiretorio Nome do arquivo previamente usado para o diretório.
	 * @param nomeDoArquivoDosBuckets Nome do arquivo previamente usado para os buckets.
	 * Caso o arquivo não tenha sido criado ainda, ele será criado com este nome.
	 * @param numeroDeRegistrosPorBucket Numero de registros por bucket caso o arquivo
	 * não tenha sido criado ainda.
	 * @param classeDaChave Classe da chave. É necessário que a classe tenha um
	 * construtor sem parâmetros.
	 * @param classeDoDado Classe do dado. É necessário que a classe tenha um
	 * construtor sem parâmetros.
	 * @param funcaoHash Função de dispersão (hash) que será usada para as chaves. É
	 * importante ressaltar que essa função só precisa gerar valores dispersos, não
	 * importando o tamanho deles. Não utilize geração de números aleatórios.
	 */
	
	public Hash(
		String nomeDoArquivoDoDiretorio,
		String nomeDoArquivoDosBuckets,
		int numeroDeRegistrosPorBucket,
		Class<TIPO_DAS_CHAVES> classeDaChave,
		Class<TIPO_DOS_DADOS> classeDoDado,
		Function<TIPO_DAS_CHAVES, Integer> funcaoHash)
	{
		funcaoHash = obterFuncaoHash(classeDaChave, funcaoHash);

		try
		{
			if (!classeDaChave.getMethod("toString")
					.getDeclaringClass().equals(classeDaChave))
			{
				IO.printlnerr("ERRO: a classe " + classeDaChave.getName() +
					" precisa implementar o método toString() para que a hash" +
					" consiga saber se duas chaves são iguais.");
			}
		}

		catch (NoSuchMethodException e)
		{
			e.printStackTrace();
		}

		diretorio = new Diretorio<>(nomeDoArquivoDoDiretorio, funcaoHash);
		
		buckets = new Buckets<>(
			nomeDoArquivoDosBuckets,
			numeroDeRegistrosPorBucket,
			classeDaChave,
			classeDoDado);
	}

	/**
	 * Cria uma Hash Extensível.
	 *
	 * @param nomeDoArquivoDoDiretorio Nome do arquivo previamente usado para o diretório.
	 * @param nomeDoArquivoDosBuckets Nome do arquivo previamente usado para os buckets.
	 * Caso o arquivo não tenha sido criado ainda, ele será criado com este nome.
	 * @param numeroDeRegistrosPorBucket Numero de registros por bucket caso o arquivo
	 * não tenha sido criado ainda.
	 * @param classeDaChave Classe da chave. É necessário que a classe tenha um
	 * construtor sem parâmetros.
	 * @param classeDoDado Classe do dado. É necessário que a classe tenha um
	 * construtor sem parâmetros.
	 */

	public Hash(
		String nomeDoArquivoDoDiretorio,
		String nomeDoArquivoDosBuckets,
		int numeroDeRegistrosPorBucket,
		Class<TIPO_DAS_CHAVES> classeDaChave,
		Class<TIPO_DOS_DADOS> classeDoDado)
	{
		this(
			nomeDoArquivoDoDiretorio,
			nomeDoArquivoDosBuckets,
			numeroDeRegistrosPorBucket,
			classeDaChave,
			classeDoDado,
			null);
	}
	
	/**
	 * Cria uma HashDinamica com capacidade de
	 * {@link #PADRAO_NUMERO_DE_REGISTROS_POR_BUCKET} registros por bucket.
	 * 
	 * @param nomeDoArquivoDoDiretorio Nome do arquivo previamente usado para o diretório.
	 * @param nomeDoArquivoDosBuckets Nome do arquivo previamente usado para os buckets.
	 * Caso o arquivo não tenha sido criado ainda, ele será criado com este nome.
	 * @param classeDaChave Classe da chave. É necessário que a classe tenha um
	 * construtor sem parâmetros.
	 * @param classeDoDado Classe do dado. É necessário que a classe tenha um
	 * construtor sem parâmetros.
	 * @param funcaoHash Função de dispersão (hash) que será usada para as chaves. É
	 * importante ressaltar que essa função só precisa gerar valores dispersos, não
	 * importando o tamanho deles. Não utilize geração de números aleatórios.
	 */
	
	public Hash(
		String nomeDoArquivoDoDiretorio,
		String nomeDoArquivoDosBuckets,
		Class<TIPO_DAS_CHAVES> classeDaChave,
		Class<TIPO_DOS_DADOS> classeDoDado,
		Function<TIPO_DAS_CHAVES, Integer> funcaoHash)
	{
		this(nomeDoArquivoDoDiretorio,
			nomeDoArquivoDosBuckets,
			PADRAO_NUMERO_DE_REGISTROS_POR_BUCKET,
			classeDaChave,
			classeDoDado,
			funcaoHash);
	}

	/**
	 * Cria uma HashDinamica com capacidade de
	 * {@link #PADRAO_NUMERO_DE_REGISTROS_POR_BUCKET} registros por bucket.
	 *
	 * @param nomeDoArquivoDoDiretorio Nome do arquivo previamente usado para o diretório.
	 * @param nomeDoArquivoDosBuckets Nome do arquivo previamente usado para os buckets.
	 * Caso o arquivo não tenha sido criado ainda, ele será criado com este nome.
	 * @param classeDaChave Classe da chave. É necessário que a classe tenha um
	 * construtor sem parâmetros.
	 * @param classeDoDado Classe do dado. É necessário que a classe tenha um
	 * construtor sem parâmetros.
	 */

	public Hash(
		String nomeDoArquivoDoDiretorio,
		String nomeDoArquivoDosBuckets,
		Class<TIPO_DAS_CHAVES> classeDaChave,
		Class<TIPO_DOS_DADOS> classeDoDado)
	{
		this(nomeDoArquivoDoDiretorio,
			nomeDoArquivoDosBuckets,
			classeDaChave,
			classeDoDado,
			null);
	}
	
	/**
	 * Obtem a quantidade máxima de bytes que as chaves podem gastar de acordo
	 * com o que foi recebido no construtor.
	 * 
	 * @return a quantidade máxima de bytes que as chaves podem gastar.
	 */
	
	public short obterQuantidadeMaximaDeBytesParaAChave()
	{
		return buckets.bucket.registroDoIndice.quantidadeMaximaDeBytesParaAChave;
	}
	
	/**
	 * Obtem a quantidade máxima de bytes que os dados podem gastar de acordo
	 * com o que foi recebido no construtor.
	 * 
	 * @return a quantidade máxima de bytes que os dados podem gastar.
	 */
	
	public short obterQuantidadeMaximaDeBytesParaODado()
	{
		return buckets.bucket.registroDoIndice.quantidadeMaximaDeBytesParaODado;
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
	
	private void inserirElementosDe(Bucket<TIPO_DAS_CHAVES, TIPO_DOS_DADOS> bucket)
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
	 * {@link Buckets#inserir(TIPO_DAS_CHAVES, TIPO_DOS_DADOS, long)}.
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
			IO.printlnerr(
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
				IO.printlnerr(
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
        			"hash.Diretorio:\n" +
        			diretorio.toString(
        				delimitadorEntreOsPonteirosDoDiretorio) + "\n"
        		) : ""
			) +
			
			"hash.Buckets:\n" +
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
