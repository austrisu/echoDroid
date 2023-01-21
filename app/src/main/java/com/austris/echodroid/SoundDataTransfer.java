package com.austris.echodroid;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;

import org.jtransforms.fft.DoubleFFT_1D;

public class SoundDataTransfer {
    private static final int SAMPLE_RATE = 44100; // samples per second
    private static final int CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO;
    private static final int AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT;
    private static final int BUFFER_SIZE = 4096;
    private static final double FREQUENCY_SPACE = 50; // The frequency space between two bits.
    private static final double BIT_0_FREQUENCY = 1000; // The frequency for bit 0.
    private static final double BIT_1_FREQUENCY = 1050; // The frequency for bit 1.

    private AudioTrack audioTrack;
    private AudioRecord audioRecord;

    public void transmitData(String data) {
        byte[] bytes = data.getBytes(); //bytes to be played
        int bufferSize = AudioTrack.getMinBufferSize(SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT);
        audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT, bufferSize, AudioTrack.MODE_STREAM);
        audioTrack.play();

        // START sequence of bits/bytes for listener to know the end of the sequence
        byte[] startSequence = new byte[] {0, 1, 1, 0};
        for (byte b : startSequence) {
            for (int i = 0; i < 8; i++) {
                double frequency = (b >> i & 1) == 0 ? BIT_0_FREQUENCY : BIT_1_FREQUENCY;
                double[] samples = generateSineWave(frequency, SAMPLE_RATE, FREQUENCY_SPACE);
                byte[] buffer = new byte[samples.length * 2];
                for (int j = 0; j < samples.length; j++) {
                    short sample = (short) (samples[j] * Short.MAX_VALUE);
                    buffer[j * 2] = (byte) (sample & 0xff);
                    buffer[j * 2 + 1] = (byte) (sample >> 8);
                }
                audioTrack.write(buffer, 0, buffer.length);
            }
        }

        for (byte b : bytes) {
            for (int i = 0; i < 8; i++) {
                double frequency = (b >> i & 1) == 0 ? BIT_0_FREQUENCY : BIT_1_FREQUENCY;
                double[] samples = generateSineWave(frequency, SAMPLE_RATE, FREQUENCY_SPACE);
                byte[] buffer = new byte[samples.length * 2];
                for (int j = 0; j < samples.length; j++) {
                    short sample = (short) (samples[j] * Short.MAX_VALUE);
                    buffer[j * 2] = (byte) (sample & 0xff);
                    buffer[j * 2 + 1] = (byte) (sample >> 8);
                }
                audioTrack.write(buffer, 0, buffer.length);
            }
        }

        // END sequence of bits/bytes for reciver to know that it is the end of listening
        byte[] endSequence = new byte[] {1, 0, 0, 1};
        for (byte b : endSequence) {
            for (int i = 0; i < 8; i++) {
                double frequency = (b >> i & 1) == 0 ? BIT_0_FREQUENCY : BIT_1_FREQUENCY;
                double[] samples = generateSineWave(frequency, SAMPLE_RATE, FREQUENCY_SPACE);
                byte[] buffer = new byte[samples.length * 2];
                for (int j = 0; j < samples.length; j++) {
                    short sample = (short) (samples[j] * Short.MAX_VALUE);
                    buffer[j * 2] = (byte) (sample & 0xff);
                    buffer[j * 2 + 1] = (byte) (sample >> 8);
                }
                audioTrack.write(buffer, 0, buffer.length);
            }
        }
        audioTrack.stop();
        audioTrack.release();
    }

    private double[] generateSineWave(double frequency, int sampleRate, double duration) {
        int numSamples = (int) (sampleRate * duration / 1000);
        double[] samples = new double[numSamples];
        for (int i = 0; i < numSamples; i++) {
            samples[i] = Math.sin(2 * Math.PI * i / (sampleRate / frequency));
        }
        return samples;
    }

//    int bufferSize;
//
//    public void startReceivingData() {
//        int bufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT);
//
//        audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT, bufferSize);
//        audioRecord.startRecording();
//    }

    public String receiveData() {
        int bufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT);

        audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT, bufferSize);
        audioRecord.startRecording();

        byte[] buffer = new byte[BUFFER_SIZE];
        int bytesRead = audioRecord.read(buffer, 0, BUFFER_SIZE);
        audioRecord.stop();
        audioRecord.release();

        byte[] bytes = new byte[bytesRead];
        System.arraycopy(buffer, 0, bytes, 0, bytesRead);

        // Decode the received bytes using FFT
        // I use FFT algorithm to detect the frequency of the signal and based on that you can extract the bits, then rebuild the bytes of the data.
        // The logic to decode the data should be based on the logic that is used to encode the data.
        // For example if FSK is used with different frequencies for 0 and 1 bits, then FFT should be used to detect the frequency of the signal,
        // and based on that bits can be extracted, then rebuild the bytes of the data.

        //Here I'm using the DoubleFFT_1D class from the edu.emory.mathcs.jtransforms.fft package to perform the FFT on the received audio data.
        DoubleFFT_1D fft = new DoubleFFT_1D(BUFFER_SIZE);
        double[] fftBuffer = new double[BUFFER_SIZE * 2];
        for (int i = 0; i < BUFFER_SIZE; i++) {
            fftBuffer[i] = (double) bytes[i];
        }
        fft.realForward(fftBuffer);

        // Find the frequency of the signal
        double maxFrequency = 0;
        int maxIndex = 0;
        for (int i = 0; i < BUFFER_SIZE; i++) {
            double frequency = i * SAMPLE_RATE / BUFFER_SIZE;
            if (fftBuffer[i] > maxFrequency) {
                maxFrequency = fftBuffer[i];
                maxIndex = i;
            }
        }
        double detectedFrequency = maxIndex * SAMPLE_RATE / BUFFER_SIZE;

        // Extract the bits and rebuild the bytes of the data
        StringBuilder sb = new StringBuilder();
        boolean inData = false;
        boolean foundStartSequence = false;
        int startSequenceIndex = 0;
        int endSequenceIndex = 0;
        byte[] startSequence = new byte[] {0, 1, 1, 0};
        byte[] endSequence = new byte[] {1, 0, 0, 1};

        for (int i = 0; i < BUFFER_SIZE; i++) {
            double frequency = i * SAMPLE_RATE / BUFFER_SIZE;
            if (!inData) {
                if (Math.abs(frequency - BIT_0_FREQUENCY) < FREQUENCY_SPACE) {
                    startSequenceIndex++;
                    if (startSequenceIndex == startSequence.length) {
                        inData = true;
                        foundStartSequence = true;
                    }
                } else {
                    startSequenceIndex = 0;
                }
            } else {
                int bit = Math.abs(frequency - BIT_0_FREQUENCY) < FREQUENCY_SPACE ? 0 : 1;
                sb.append(bit);
                if (sb.length() == 8) {
                    byte b = (byte) Integer.parseInt(sb.toString(), 2);
                    sb.setLength(0);
                }
                if ((bit == endSequence[endSequenceIndex]) && (sb.length() % 8 == 0)) {
                    endSequenceIndex++;
                    if (endSequenceIndex == endSequence.length) {
                        break;
                    }
                } else {
                    endSequenceIndex = 0;
                }
            }
        }

        if (foundStartSequence) {
            return new String(bytes);
        } else {
            return "";
        }
    }
}


