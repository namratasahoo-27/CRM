package crm.utils;

import java.io.File;

/**
 * @deprecated This class uses GUI components (JFileChooser) which are incompatible with containerized environments.
 * Use file upload endpoints with MultipartFile instead for server-side applications.
 * This class is maintained only for backward compatibility with test code.
 */
@Deprecated
public class ReadDataUtils {

    /**
     * @deprecated Use file upload endpoints with @RequestParam("file") MultipartFile instead.
     * This method requires a GUI environment and will throw HeadlessException in containers.
     */
    @Deprecated
    public static File ReadFile(String dialogMEssage, Object parent, String fileExtensionDescription,
                                String... fileExtension) {
        throw new UnsupportedOperationException(
            "ReadFile() is not supported in containerized environments. " +
            "Use file upload endpoints with MultipartFile instead."
        );
    }

}
