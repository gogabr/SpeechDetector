/**
 * 
 * Funf: Open Sensing Framework
 * Copyright (C) 2010-2011 Nadav Aharony, Wei Pan, Alex Pentland.
 * Acknowledgments: Alan Gardner
 * Contact: nadav@media.mit.edu
 * 
 * This file is part of Funf.
 * 
 * Funf is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version.
 * 
 * Funf is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with Funf. If not, see <http://www.gnu.org/licenses/>.
 * 
 */
package edu.mit.media.funf.math;


public class MFCC
{
	private static float minMelFreq = 40;  // should be configurable
	private static float maxMelFreq = 3800; // should be configurable
	//private static float lifterExp = 0.6f;
	private static float lifterQ = 22.0f;	// should be configurable; copied from Kaldi
	private int numCoeffs;
	private int melBands;
	private int numFreqs;
	private float sampleRate;
	public Matrix melWeights = null;
	public Matrix dctMat = null;
	public float[] lifterWeights;

	public MFCC(int fftSize, int numCoeffs, int melBands, float sampleRate)
	{
	    // Precompute mel-scale auditory perceptual spectrum
	    melWeights = new Matrix(melBands, fftSize, 0);
	    
	    // Number of non-redundant frequency bins
	    numFreqs = fftSize/2 + 1;
	    this.numCoeffs = numCoeffs;
	    this.melBands = melBands;
	    this.sampleRate = sampleRate;
	    
	    float fftFreqs[] = new float[fftSize];
	    for (int i = 0; i < fftSize; i ++)
	    {
	    	fftFreqs[i] = (float)i/(float)fftSize*this.sampleRate;
	    }
	    
	    float minMel = fhz2mel(minMelFreq);
	    float maxMel = fhz2mel(maxMelFreq);
	    
	    float binFreqs[] = new float[melBands + 2];
	    for (int i = 0; i < melBands + 2; i ++)
	    {
	    	binFreqs[i] = fmel2hz(minMel + (float)i/((float)melBands + 1.0f) * (maxMel - minMel));
	    }
	    
	    for (int i = 0; i < melBands; i ++)
	    {
	    	for (int j = 0; j < fftSize; j ++)
	    	{
	    		float loSlope = (fftFreqs[j] - binFreqs[i])/(binFreqs[i+1] - binFreqs[i]);
		    	float hiSlope = (binFreqs[i+2] - fftFreqs[j])/(binFreqs[i+2] - binFreqs[i+1]);
		    	melWeights.A[i][j] = Math.max(0, Math.min(loSlope, hiSlope));
	    	}
	    }
	    
	    // Keep only positive frequency parts of Fourier transform
	    melWeights = melWeights.getMatrix(0, melBands - 1, 0, numFreqs - 1);
	    
	    // Precompute DCT matrix
	    dctMat = new Matrix(numCoeffs, melBands, 0);
	    float scale = (float)Math.sqrt(2.0/melBands);
	    for (int i = 0; i < numCoeffs; i ++)
	    {
	    	for (int j = 0; j < melBands; j ++)
	    	{
	    		float phase = j*2 + 1;
		    	dctMat.A[i][j] = (float)Math.cos((float)i*phase/(2.0f*(float)melBands)*(float)Math.PI)*scale;
	    	}
	    }
	    float root2 = 1.0f/(float)Math.sqrt(2.0);
	    for (int j = 0; j < melBands; j ++)
	    {
	    	dctMat.A[0][j] *= root2;
	    }
	    
	    // Precompute liftering vector
	    lifterWeights = new float[numCoeffs];
	    lifterWeights[0] = 1.0f;
	    for (int i = 1; i < numCoeffs; i ++)
	    {
//	    	lifterWeights[i] = (float)Math.pow((float)i, lifterExp);
            lifterWeights[i] = (float)(1.0 + 0.5 * lifterQ * Math.sin(Math.PI * i / lifterQ));  // copied from Kaldi
	    }
	}
	
	public float[] cepstrum(float[] re, float[] im)
	{
		Matrix powerSpec = new Matrix(numFreqs, 1);
		for (int i = 0; i < numFreqs; i ++)
		{
			powerSpec.A[i][0] = re[i]*re[i] + im[i]*im[i];
		}
//		/**/dumpVector("power spectrum", powerSpec, numFreqs);

		// melWeights - melBands x numFreqs
		// powerSpec  - numFreqs x 1
		// melWeights*powerSpec - melBands x 1
		// aSpec      - melBands x 1
		// dctMat     - numCoeffs x melBands
		// dctMat*log(aSpec) - numCoeffs x 1
		
		Matrix aSpec = melWeights.times(powerSpec);
		Matrix logMelSpec = new Matrix(melBands, 1);
		for (int i = 0; i < melBands; i ++)
		{
			logMelSpec.A[i][0] = (float)Math.log(aSpec.A[i][0]);
		}

//		/**/dumpVector("log mel", logMelSpec, melBands);

		Matrix melCeps = dctMat.times(logMelSpec);
			
		float[] ceps = new float[numCoeffs];
		for (int i = 0; i < numCoeffs; i ++)
		{
			ceps[i] = lifterWeights[i]*melCeps.A[i][0];
		}

		return ceps;
	}

	
	public float fmel2hz(float mel)
	{
		return 700.0f*(float)(Math.exp(mel/1127.0) - 1.0);
	}
	
	public float fhz2mel(float freq)
	{
		return 1127.0f*(float)Math.log(1.0 + freq/700.0);
	}

    /**/ private void dumpVector(String header, Matrix m, int size)
    {
        System.out.print(header + ":");
        for (int i = 0; i < size; i++) {
            System.out.print(" ");
            System.out.print(m.A[i][0]);
        }
        System.out.println();
    }

}
