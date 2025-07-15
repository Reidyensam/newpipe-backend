import static spark.Spark.*;
import com.google.gson.Gson;
import org.schabi.newpipe.extractor.NewPipe;
import org.schabi.newpipe.extractor.ServiceList;
import org.schabi.newpipe.extractor.stream.StreamInfo;
import org.schabi.newpipe.extractor.stream.StreamSearchInfo;
import org.schabi.newpipe.extractor.stream.StreamType;
import org.schabi.newpipe.extractor.stream.VideoStream;
import org.schabi.newpipe.extractor.stream.AudioStream;

import java.util.*;

public class Main {
    public static void main(String[] args) {
        port(8080);
        Gson gson = new Gson();

        // 🔍 Búsqueda: /search?q=belanova
        get("/search", (req, res) -> {
            res.type("application/json");
            String query = req.queryParams("q");

            StreamSearchInfo searchResult = StreamSearchInfo.getInfo(
                ServiceList.Youtube, query);

            List<Map<String, String>> results = new ArrayList<>();

            for (StreamInfo item : searchResult.getRelatedStreams()) {
                if (item.getStreamType() == StreamType.VIDEO) {
                    Map<String, String> video = new HashMap<>();
                    video.put("id", item.getUrl().split("v=")[1]);
                    video.put("title", item.getName());
                    video.put("thumbnail", item.getThumbnailUrl());
                    results.add(video);
                }
            }

            return gson.toJson(Map.of("results", results));
        });

        // 🎥 Obtener audio/video: /video/:id
        get("/video/:id", (req, res) -> {
            res.type("application/json");
            String videoId = req.params("id");

            StreamInfo info = StreamInfo.getInfo(ServiceList.Youtube,
                    "https://www.youtube.com/watch?v=" + videoId);

            Optional<VideoStream> video = info.getVideoStreams().stream().findFirst();
            Optional<AudioStream> audio = info.getAudioStreams().stream().findFirst();

            Map<String, String> response = new HashMap<>();
            response.put("title", info.getName());
            response.put("thumbnail", info.getThumbnailUrl());
            response.put("videoUrl", video.map(VideoStream::getUrl).orElse(""));
            response.put("audioUrl", audio.map(AudioStream::getUrl).orElse(""));

            return gson.toJson(response);
        });
    }
}