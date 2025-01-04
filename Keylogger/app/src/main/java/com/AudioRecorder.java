import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class AudioRecorder {
    private static final int SAMPLE_RATE = 44100; // Sample rate in Hz
    private static final int CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO;
    private static final int AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT;
    private static final int BUFFER_SIZE = AudioRecord.getMinBufferSize(SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT);
    private static final String SERVER_URL = "http://10.16.50.148/upload:5000"; // Change this to your actual server URL

    private AudioRecord audioRecord;
    private boolean isRecording = false;

    public void startRecording() {
        // Create the AudioRecord instance
        audioRecord = new AudioRecord(
            MediaRecorder.AudioSource.MIC,
            SAMPLE_RATE,
            CHANNEL_CONFIG,
            AUDIO_FORMAT,
            BUFFER_SIZE
        );

        // File to save recorded audio
        File outputFile = new File(Environment.getExternalStorageDirectory(), "audio.pcm");

        // Create a new thread to record audio
        new Thread(() -> {
            byte[] audioData = new byte[BUFFER_SIZE];
            try (FileOutputStream fos = new FileOutputStream(outputFile)) {
                audioRecord.startRecording();
                isRecording = true;
                long startTime = System.currentTimeMillis();
                while (isRecording && System.currentTimeMillis() - startTime < 10000) { // 10 seconds
                    int read = audioRecord.read(audioData, 0, audioData.length);
                    if (read > 0) {
                        fos.write(audioData, 0, read);
                    }
                }
            } catch (IOException e) {
                Log.e("AudioRecorder", "Error recording audio", e);
            } finally {
                stopRecording();
                // After recording, send the file to the server
                sendFileToServer(outputFile);
            }
        }).start();
    }

    public void stopRecording() {
        if (audioRecord != null) {
            audioRecord.stop();
            audioRecord.release();
            isRecording = false;
        }
    }

    private void sendFileToServer(File file) {
        try {
            // Set up a connection to the server
            URL url = new URL(SERVER_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/octet-stream");

            // Read the PCM file into a byte array
            byte[] fileData = new byte[(int) file.length()];
            try (java.io.FileInputStream fileInputStream = new java.io.FileInputStream(file)) {
                fileInputStream.read(fileData);
            }

            // Send the file data to the server
            try (OutputStream outputStream = connection.getOutputStream()) {
                outputStream.write(fileData);
                outputStream.flush();
            }

            // Check if the request was successful
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                Log.d("AudioRecorder", "File uploaded successfully");
            } else {
                Log.e("AudioRecorder", "Failed to upload file. Response code: " + responseCode);
            }

            connection.disconnect();

        } catch (IOException e) {
            Log.e("AudioRecorder", "Error sending file to server", e);
        }
    }
}
