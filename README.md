# echoDroid

Audio data transfer is a method of transmitting data using inaudible sound waves. The data is encoded into an audio signal, which is then transmitted via speakers or microphones. On the receiving end, the audio signal is decoded to retrieve the original data.

This method of data transfer can be useful in situations where other forms of data transfer (such as Bluetooth or Wi-Fi) are not available or unreliable. For example, audio data transfer can be used to transfer data between devices in a location where radio frequency interference is high, or to transfer data in a secure environment where other forms of data transfer may be monitored.

Audio data transfer can be done using different technologies such as ultrasonic and frequency-shift keying. In ultrasonic data transfer, the audio signal is transmitted at a frequency above the range of human hearing (typically above 20kHz) and can be received by a device with a microphone capable of detecting these high frequencies. In frequency-shift keying, the data is encoded into changes in frequency of the audio signal, which can be received by a device with a microphone and a decoder to extract the original data.

This technology is not yet widely used but it's gaining popularity in some specific fields such as mobile banking and payments, where it's used to transfer sensitive data in a secure way.

## Frequency-shift keying

Frequency-shift keying (FSK) is a method of encoding digital data onto an analog audio signal by shifting the frequency of the signal between two or more discrete values.

The two most common forms of FSK are binary FSK (BFSK) and frequency-shift keying modulation (FSKM). In BFSK, the two frequencies used are typically separated by a constant frequency offset, such as 1kHz. One frequency represents a binary "1" and the other represents a binary "0". In the case of FSKM, the number of different frequencies used is greater than two, and each frequency represents a different data symbol.

In the receiver side, the audio signal is demodulated back to the original digital data by measuring the frequency of the incoming signal. The receiver compares the frequency of the incoming signal to the known frequencies used for the "1" and "0" symbols and determines the corresponding digital data.

FSK is relatively simple and robust, and can be used for data transfer over a wide range of frequencies and distances. It is also relatively immune to noise and interference, as the receiver can still correctly determine the data symbol even if the frequency of the signal is slightly off.

FSK is used in a variety of applications such as wireless data communications, radio-controlled devices, and remote control systems. It's also used in some audio data transfer technologies to transfer data between devices.

## Code explanation

This is minimal code to transfer text between two devices using sound.

In this example, I'm using the AudioTrack class to generate and play sound. Each byte of data is sent as 8 bits, where each bit is represented by a different frequency. Bit 0 is represented by a frequency of 1000 Hz and bit 1 is represented by a frequency of 1050 Hz, this is a simple example you can use other frequency values. Each bit is sent for a certain duration of time determined by the FREQUENCY_SPACE variable.

To receive this data you can use the AudioRecord class to record the sound, then use a FFT algorithm to detect the frequency of the signal and based on that you can extract the bits, then rebuild the bytes of the data. However, this process is a bit more complex and I can provide an example if you would like.





## Dependencies

The `edu.emory.mathcs.jtransforms.fft` package is a Java library that provides FFT implementations, you can use it to perform FFT on your audio data.

Here are the steps to install, register, and use the `edu.emory.mathcs.jtransforms.fft` package in your Android app:

1. Download the library from the official website(http://sites.google.com/site/piotrwendykier/software/jtransforms)
2. Copy the downloaded .jar file to the `libs` folder of your Android project.
3. Right-click on the .jar file in your project and select `Add as Library`
4. In your project's `build.gradle` file, add the following line in the dependencies section:

```
implementation files('libs/jtransforms-3.1-with-dependencies.jar')
```

1. In your code, import the package by adding the following line at the top of your file:

```
import edu.emory.mathcs.jtransforms.fft.DoubleFFT_1D;
```

1. Now you can use the `DoubleFFT_1D` class to perform FFT on your audio data.

More info https://www.geeksforgeeks.org/how-to-add-external-library-in-android-studio/