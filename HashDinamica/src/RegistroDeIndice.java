
public class RegistroDeIndice<TIPO_DAS_CHAVES, TIPO_DOS_DADOS>
{
	char lapide;
	TIPO_DAS_CHAVES chave;
	TIPO_DOS_DADOS dado;
	
	private RegistroDeIndice(char lapide, TIPO_DAS_CHAVES chave, TIPO_DOS_DADOS dado)
	{
		this.lapide = lapide;
		this.chave = chave;
		this.dado = dado;
	}
	
	public static int obterTamanhoEmBytes()
	{
		return Character.BYTES + Object.
	}
}
