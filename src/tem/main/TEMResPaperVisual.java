package tem.main;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

import tem.conf.PathConfig;

public class TEMResPaperVisual {

	/**
	 * @param args
	 * @throws IOException 
	 * @throws Exception 
	 */
	public static void main(String[] args) throws IOException, Exception {
		// TODO Auto-generated method stub
		String modelFile = PathConfig.modelResPath + "ServerTEMRes/Model_E10_T15.model";

		//Get TEM model result
		TEMModel model = new TEMModel();
		// load model
		System.out.println("reading a class from : " + modelFile);
		FileInputStream fis = new FileInputStream(modelFile);
		ObjectInputStream ois = new ObjectInputStream(fis);
		model = (TEMModel) ois.readObject();
		ois.close();
		System.out.println(model.K);
		System.out.println(model.ENum);
		System.out.println("mu");
		for(int e = 0; e < model.ENum; e++){
			System.out.println(model.fgmm.p_mu[e][0]);
		}
		System.out.println("lambda");
		for(int e = 0; e < model.ENum; e++){
			System.out.println(model.fgmm.p_lambda[e][0]);
		}


	}

}
