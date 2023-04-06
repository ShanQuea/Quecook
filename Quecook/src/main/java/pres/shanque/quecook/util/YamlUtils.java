package pres.shanque.quecook.util;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.representer.Representer;
import pres.shanque.quecook.recipe.entity.FoodRecipe;
import pres.shanque.quecook.util.asserts.Asserts;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * <p>yaml 相关工具</p>
 */
public abstract class YamlUtils {

    public static void main(String[] args) throws URISyntaxException, IOException {
        System.out.println(toBeanMap("file:/D:\\java插件demo\\QueCook\\Quecook\\src\\main\\resources\\recipe.yml", FoodRecipe.class));
    }

    private static final Yaml YAML;

    static {
//        Representer representer = new Representer();
//        representer.getPropertyUtils().setSkipMissingProperties(true);
        final DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        options.setPrettyFlow(true);
        options.setIndent(4);
        options.setIndicatorIndent(2);
        YAML = new Yaml(options);
    }

    public static <T> Map<String, T> toBeanMap(String location, Class<T> beanType) throws URISyntaxException, IOException {
        Asserts.hasText(location, "location must not be empty");
        Asserts.notNull(beanType, "beanType must not be null");

        return toBeanMap(new URI(
                location.replace('\\', '/')
                        .replace(" ", "%20")), beanType);
    }

    public static <T> Map<String, T> toBeanMap(URI uri, Class<T> beanType) throws IOException {
        Asserts.notNull(uri, "uri must not be null");
        Asserts.notNull(beanType, "beanType must not be null");

        final URL url = uri.toURL();
        final URLConnection urlConnection = Objects.requireNonNull(url).openConnection();
        urlConnection.connect();
        return toBeanMap(urlConnection.getInputStream(), beanType);
    }

    public static <T> Map<String, T> toBeanMap(InputStream inputStream, Class<T> beanType) throws IOException {
        Asserts.notNull(inputStream, "inputStream must not be null");
        Asserts.notNull(beanType, "beanType must not be null");

        final Map<String, Object> beanMap;
        try (InputStream is = inputStream) {
            beanMap = YAML.load(is);
        }

        final Map<String, T> resultMap = new HashMap<>(beanMap.size());
        for (final String key : beanMap.keySet()) {
            resultMap.put(key, YAML.loadAs(YAML.dump(beanMap.get(key)), beanType));
        }

        return resultMap;
    }
}
