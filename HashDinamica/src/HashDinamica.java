import java.util.function.Function;

/**
 * Estrutura de hashing dinâmico para indexamento de registros.
 * 
 * @author Axell Brendow ( https://github.com/axell-brendow )
 */

public class HashDinamica<TIPO_DAS_CHAVES, TIPO_DOS_DADOS>
{
	Diretorio diretorio;
	Buckets<TIPO_DAS_CHAVES, TIPO_DOS_DADOS> buckets;
	
	public HashDinamica(
			String nomeDoArquivoDoDiretorio,
			String nomeDoArquivoDosBuckets,
			int numeroDeRegistrosPorBucket,
			int quantidadeDeBytesParaAsChaves,
			int quantidadeDeBytesParaOsValores,
			Function<TIPO_DAS_CHAVES, Integer> funcaoHash)
	{
		diretorio = new Diretorio(nomeDoArquivoDoDiretorio);
		buckets = new Buckets<>(nomeDoArquivoDosBuckets, numeroDeRegistrosPorBucket, quantidadeDeBytesParaAsChaves, quantidadeDeBytesParaOsValores);
	}
}
