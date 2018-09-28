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

public class Window
{
	private float[] window;
	private int n;
	
	public Window(int windowSize)
	{
		n = windowSize;

		window = new float[n];
//		// Make a Hamming window
//		for(int i = 0; i < n; i++)
//		{
//			window[i] = 0.54f - 0.46f*(float)Math.cos(2*Math.PI*(float)i/((float)n-1));
//		}
		// Make a Povey window
		double a = 2.0 * Math.PI / (windowSize-1);
		for (int i = 0; i < n; i++) {
			window[i] = (float)Math.pow(0.5 - 0.5*Math.cos(a * (double)i), 0.85);
		}
	}

	public void applyWindow(float[] buffer)
	{
		for (int i = 0; i < n; i ++)
		{
			buffer[i] *= window[i];
		}
	}

}
