package calsim.surrogate.examples;

import java.util.ArrayList;
import java.util.Arrays;

import calsim.surrogate.AggregateMonths;
import calsim.surrogate.DisaggregateMonths;
import calsim.surrogate.DisaggregateMonthsDaysToOps;
import calsim.surrogate.DisaggregateMonthsRepeat;
import calsim.surrogate.DisaggregateMonthsSpline;
import calsim.surrogate.LinearConstraint;
import calsim.surrogate.MockSurrogate;
import calsim.surrogate.SalinitySurrogateManager;
import calsim.surrogate.Surrogate;
import calsim.surrogate.SurrogateMonth;

public class CalSIMExampleEmmatonInterface {
	public SalinitySurrogateManager manager = SalinitySurrogateManager.INSTANCE;

	/** This is the array version of the original CalSIM call 
	 *  This function is very calsim-like and you could airlift it*/
	public float annec_arr(float[] Qsac_prv, float Qsac_est, float[] Qexp_prv, float Qexp_est, float[] Qsjr_prv,
			float Qsjr_fut, float[] DXC_prv, float DXC_fut, float[] DICU_prv, float DICU_fut, float[] Qsac_oth_prv,
			float Qsac_oth_fut, float[] Qexp_oth_prv, float Qexp_oth_fut, float[] SMSCG_prv, float SMSCG_fut,
			float ECTARGET, int location, int variable, int ave_type, int currMonth, int currYear) {

		int NHIST = 5; // Number of months, which includes 1 current and 4 past
		int NLOC = 7; // TODO move to config?

		double[][] sac = new double[1][NHIST];
		double[][] exp = new double[1][NHIST];
		double[][] dcc = new double[1][NHIST];
		double[][] dcd = new double[1][NHIST];
		double[][] sjr = new double[1][NHIST];
		double[][] tide = new double[1][NHIST];
		double[][] smscg = new double[1][NHIST];

		// Batching isn't very useful here, and thus hard to remember what this index
		// is for. This is just a reminder it is for batching.
		final int BATCHZERO = 0;
		sac[BATCHZERO][0] = (double) (Qsac_est + Qsac_oth_fut);
		exp[BATCHZERO][0] = (double) (Qexp_est + Qexp_oth_fut);
		dcc[BATCHZERO][0] = (double) DXC_fut;
		dcd[BATCHZERO][0] = (double) DICU_fut;
		tide[BATCHZERO][0] = 6.; // TODO
		smscg[BATCHZERO][0] = (double) SMSCG_fut;

		for (int ihist = 1; ihist < NHIST; ihist++) {
			sac[BATCHZERO][ihist] = (double) (Qsac_prv[ihist-1] + Qsac_oth_prv[ihist-1]); // Add "other" to "regular" Sac
			exp[BATCHZERO][ihist] = (double) (Qexp_prv[ihist-1] + Qexp_oth_prv[ihist-1]); // TODO double check time direction?
			dcc[BATCHZERO][ihist] = (double) DXC_prv[ihist-1];
			dcd[BATCHZERO][ihist] = (double) DICU_prv[ihist-1];
			sjr[BATCHZERO][ihist] = (double) Qsjr_prv[ihist-1];
			tide[BATCHZERO][ihist] = 6.; // TODO
			smscg[BATCHZERO][ihist] = (double) SMSCG_prv[ihist-1];
		}
		ArrayList<double[][]> monthlyInput = new ArrayList<double[][]>(
				Arrays.asList(sac, exp, dcc, dcd, sjr, tide, smscg));

		ave_type = 0; // TODO hardwired to mean, which isn't right here
		float out = manager.annEC(monthlyInput, location, variable, ave_type, currMonth, currYear);
		return out;
	}

	/**
	 * 
	 * @param Qsac_prv     Array of four past monthly values Sac flow from one month
	 *                     ago to four (reverse chron)
	 * @param Qsac_est     Nominal value for current month main Sac flow
	 * @param Qexp_prv     Four past exports
	 * @param Qexp_est     Nominal for current month exports
	 * @param Qsjr_prv     Four past San Joaquin
	 * @param Qsjr_fut     Estimate for current month SJR
	 * @param DXC_prv      Four Past DXC gate positions, where 1.0 is open and 0.0
	 *                     is closed
	 * @param DXC_fut      Current month DXC gate position
	 * @param DICU_prv     Four past month consumptive use net total
	 * @param DICU_fut     Current month consumptive use
	 * @param Qsac_oth_prv Four past month other northern flows (not ones being
	 *                     varied)
	 * @param Qsac_oth_fut Current month
	 * @param Qexp_oth_prv
	 * @param Qexp_oth_fut
	 * @param SMSCG_prv
	 * @param SMSCG_fut
	 * @param ECTARGET
	 * @param location
	 * @param variable
	 * @param ave_type
	 * @param currMonth
	 * @param currYear
	 * @return
	 */
	public float linegen_arr(float[] Qsac_prv, float Qsac_est, float[] Qexp_prv, float Qexp_est, float[] Qsjr_prv,
			float Qsjr_fut, float[] DXC_prv, float DXC_fut, float[] DICU_prv, float DICU_fut, float[] Qsac_oth_prv,
			float Qsac_oth_fut, float[] Qexp_oth_prv, float Qexp_oth_fut, float[] SMSCG_prv, float SMSCG_fut,
			float ECTARGET, int location, int variable, int ave_type, int currMonth, int currYear) {

		int NHIST = 5;
		int NLOC = 7; // TODO move to config?
		double[][] sac = new double[1][NHIST];
		double[][] exp = new double[1][NHIST];
		double[][] dcc = new double[1][NHIST];
		double[][] dcd = new double[1][NHIST];
		double[][] sjr = new double[1][NHIST];
		double[][] tide = new double[1][NHIST];
		double[][] smscg = new double[1][NHIST];

		// Batching isn't very useful here, and thus hard to remember what this index
		// is for. This is just a reminder it is for batching.
		final int BATCHZERO = 0;
		sac[BATCHZERO][0] = (double) (Qsac_est+Qsac_oth_fut);
		exp[BATCHZERO][0] = (double) (Qexp_est+Qexp_oth_fut);
		dcc[BATCHZERO][0] = (double) DXC_fut;
		dcd[BATCHZERO][0] = (double) DICU_fut;
		tide[BATCHZERO][0] = -999.;
		smscg[BATCHZERO][0] = (double) SMSCG_fut;

		for (int ihist = 1; ihist < NHIST; ihist++) {
			sac[BATCHZERO][ihist] = (double) (Qsac_prv[ihist-1] + Qsac_oth_prv[ihist-1]); // Add "other" to "regular" Sac
			exp[BATCHZERO][ihist] = (double) (Qexp_prv[ihist-1] + Qexp_oth_prv[ihist-1]); // TODO double check time direction?
			dcc[BATCHZERO][ihist] = (double) DXC_prv[ihist-1];
			dcd[BATCHZERO][ihist] = (double) DICU_prv[ihist-1];
			sjr[BATCHZERO][ihist] = (double) Qsjr_prv[ihist-1];
			tide[BATCHZERO][ihist] = 0.;
			smscg[BATCHZERO][ihist] = (double) SMSCG_prv[ihist-1];
		}

		ArrayList<double[][]> monthlyInput = new ArrayList<double[][]>(
				Arrays.asList(sac, exp, dcc, dcd, sjr, tide, smscg));

		ave_type = 0; // TODO hardwired to mean, which isn't right here
		float out = (float) manager.lineGenImpl(monthlyInput, location, variable, ave_type, currMonth, currYear,Qsac_est,Qexp_est,ECTARGET);	
		
		return out;
	}

	public static void main(String[] args) {

		CalSIMExampleEmmatonInterface calsimIF = new CalSIMExampleEmmatonInterface();

		SurrogateMonth surrogateMonth = EmmatonExampleTensorFlowANN.emmatonSurrogateMonth();

		float[] sac = { 6990.f, 10500.f, 9500.f, 18000.f };
		float sac_est = 7771.f;
		float[] exp = { 2750.f, 6300.f, 4200.f, 11700.f };
		float exp_est = 3500.f;
		float[] dcc = { 0.0f, 0.0f, 30.f, 31.f };
		float dcc_fut = 0.0f;
		float[] dcd = { 1360.f, 895.f, 2029.f, 2565.f };
		float dcd_fut = 1631.f;
		float[] sjr = { 1347.f, 1205.f, 1120.f, 920.f };
		float sjr_fut = 1277.f;
		float[] tide = { 6.560f, 6.184f, 5.508f, 5.083f, 6.913f };
		float tide_fut = 6.56f;
		float[] smscg = { 0.000f, 0.000f, 1.000f, 1.000f };
		float smscg_fut = 0.0f;

		float[] sac_other_prev = { 0.f,0.f,0.f,0.f,0.f };
		float sac_other = 0.f; // TODO account for this and check the order
		float[] exp_other_prev = { 0.f,0.f,0.f,0.f,0.f };
		float exp_other = 0.f;

		float ecTarget = 2500.f;
		int location = 3;
		int variable = 0;
		int ave_type = 0;
		int currMonth = 8;
		int currYear = 1994;
		float out = calsimIF.annec_arr(sac, sac_est, exp, exp_est, sjr, sjr_fut, dcc, dcc_fut, dcd, dcd_fut,
				sac_other_prev, sac_other, exp_other_prev, exp_other, smscg, smscg_fut, ecTarget, location, variable,
				ave_type, currMonth, currYear);
		System.out.println("Out: " + out );

		
		
		
		
		variable = LinearConstraint.VAL_NDX;
		float lineout0 = calsimIF.linegen_arr(sac, sac_est, exp, exp_est, sjr, sjr_fut, dcc, dcc_fut, dcd, dcd_fut,
				sac_other_prev, sac_other, exp_other_prev, exp_other, smscg, smscg_fut, ecTarget, location, variable,
				ave_type, currMonth, currYear);
		System.out.println(lineout0);

		variable = LinearConstraint.DSAC_NDX;
		float lineout1 = calsimIF.linegen_arr(sac, sac_est, exp, exp_est, sjr, sjr_fut, dcc, dcc_fut, dcd, dcd_fut,
				sac_other_prev, sac_other, exp_other_prev, exp_other, smscg, smscg_fut, ecTarget, location, variable,
				ave_type, currMonth, currYear);
		System.out.println(lineout1);
		
		variable = LinearConstraint.DEXP_NDX;
		float lineout2 = calsimIF.linegen_arr(sac, sac_est, exp, exp_est, sjr, sjr_fut, dcc, dcc_fut, dcd, dcd_fut,
				sac_other_prev, sac_other, exp_other_prev, exp_other, smscg, smscg_fut, ecTarget, location, variable,
				ave_type, currMonth, currYear); 
		System.out.println(lineout2);

	}

}
