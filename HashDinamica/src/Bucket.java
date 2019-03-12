import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Classe que gerencia um bucket específico.
 * 
 * @author Axell Brendow ( https://github.com/axell-brendow )
 */

public class Bucket<TIPO_DAS_CHAVES, TIPO_DOS_DADOS> implements Serializavel
{
	byte profundidadeLocal;
	int numeroDeRegistrosPorBucket;
	RegistroDeIndice<TIPO_DAS_CHAVES, TIPO_DOS_DADOS>[] registrosDosIndices;
	
	private Bucket(byte profundidadeLocal, int numeroDeRegistrosPorBucket, RegistroDeIndice<TIPO_DAS_CHAVES, TIPO_DOS_DADOS>[] registrosDosIndices)
	{
		this.profundidadeLocal = profundidadeLocal;
		this.numeroDeRegistrosPorBucket = numeroDeRegistrosPorBucket;
		this.registrosDosIndices = registrosDosIndices;
	}
	
	@Override
	public void escreverObjeto(ObjectOutputStream correnteDeSaida)
	{
		if (correnteDeSaida != null)
		{
			byte[] bytes = obterBytes();
			
			try
			{
				correnteDeSaida.write(bytes);
			}
			
			catch (IOException ioex)
			{
				ioex.printStackTrace();
			}
		}
	}
	
	@Override
	public void lerObjeto(ObjectInputStream correnteDeEntrada)
	{
		if (correnteDeEntrada != null)
		{
			byte[] bytes = correnteDeEntrada.read();
			
			try
			{
				correnteDeSaida.write(bytes);
			}
			
			catch (IOException ioex)
			{
				ioex.printStackTrace();
			}
		}
	}
	
	@Override
	public byte[] obterBytes()
	{
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void lerBytes(byte[] bytes)
	{
		// TODO Auto-generated method stub
		
	}
}
