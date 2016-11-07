package cc.gavin.grumman.zeta.render;

import com.jfinal.kit.PathKit;
import com.jfinal.kit.StrKit;
import com.jfinal.render.Render;
import com.jfinal.render.RenderException;
import com.jfinal.render.RenderFactory;

import javax.servlet.ServletContext;
import javax.servlet.ServletOutputStream;
import java.io.*;

/**
 * Created by user on 11/7/16.
 */
public class StaticResourceRender extends Render {
    private static final String DEFAULT_CONTENT_TYPE = "application/octet-stream";
    private File file;
    private static String fileDownloadPath;
    private static ServletContext servletContext;
    private static String webRootPath;

    public StaticResourceRender(File file) {
        this.file = file;
    }

    public StaticResourceRender(String fileName,ServletContext servletContext) {
        this.servletContext = servletContext;
        webRootPath = PathKit.getWebRootPath();
        fileName = fileName.startsWith("/")?webRootPath + fileName:fileDownloadPath + fileName;
        System.out.println(fileName);
        this.file = new File(fileName);
    }

    /*static void init(String fileDownloadPath, ServletContext servletContext) {
        fileDownloadPath = "";
        servletContext = servletContext;
        webRootPath = PathKit.getWebRootPath();
        System.out.println("///////////");
        System.out.println(webRootPath);
    }*/

    public void render() {
        if(this.file != null && this.file.isFile()) {
            this.response.setHeader("Accept-Ranges", "bytes");
            this.response.setHeader("Content-disposition", "attachment; filename=" + this.encodeFileName(this.file.getName()));
            String contentType = servletContext.getMimeType(this.file.getName());
            this.response.setContentType(contentType != null?contentType:"application/octet-stream");
            if(StrKit.isBlank(this.request.getHeader("Range"))) {
                this.normalRender();
            } else {
                this.rangeRender();
            }

        } else {
            RenderFactory.me().getErrorRender(404).setContext(this.request, this.response).render();
        }
    }

    private String encodeFileName(String fileName) {
        try {
            return new String(fileName.getBytes("GBK"), "ISO8859-1");
        } catch (UnsupportedEncodingException var3) {
            return fileName;
        }
    }

    private void normalRender() {
        this.response.setHeader("Content-Length", String.valueOf(this.file.length()));
        BufferedInputStream inputStream = null;
        ServletOutputStream outputStream = null;

        try {
            inputStream = new BufferedInputStream(new FileInputStream(this.file));
            outputStream = this.response.getOutputStream();
            byte[] e = new byte[1024];
            boolean len = true;

            int len1;
            while((len1 = inputStream.read(e)) != -1) {
                outputStream.write(e, 0, len1);
            }

            outputStream.flush();
        } catch (IOException var18) {
            if(getDevMode()) {
                throw new RenderException(var18);
            }
        } catch (Exception var19) {
            throw new RenderException(var19);
        } finally {
            if(inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException var17) {
                    ;
                }
            }

            if(outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException var16) {
                    ;
                }
            }

        }

    }

    private void rangeRender() {
        Long[] range = new Long[]{null, null};
        this.processRange(range);
        String contentLength = String.valueOf(range[1].longValue() - range[0].longValue() + 1L);
        this.response.setHeader("Content-Length", contentLength);
        this.response.setStatus(206);
        StringBuilder contentRange = (new StringBuilder("bytes ")).append(String.valueOf(range[0])).append("-").append(String.valueOf(range[1])).append("/").append(String.valueOf(this.file.length()));
        this.response.setHeader("Content-Range", contentRange.toString());
        BufferedInputStream inputStream = null;
        ServletOutputStream outputStream = null;

        try {
            long e = range[0].longValue();
            long end = range[1].longValue();
            inputStream = new BufferedInputStream(new FileInputStream(this.file));
            if(inputStream.skip(e) != e) {
                throw new RuntimeException("File skip error");
            }

            outputStream = this.response.getOutputStream();
            byte[] buffer = new byte[1024];
            long position = e;

            while(true) {
                int len;
                while(position <= end && (len = inputStream.read(buffer)) != -1) {
                    if(position + (long)len <= end) {
                        outputStream.write(buffer, 0, len);
                        position += (long)len;
                    } else {
                        for(int i = 0; i < len && position <= end; ++i) {
                            outputStream.write(buffer[i]);
                            ++position;
                        }
                    }
                }

                outputStream.flush();
                break;
            }
        } catch (IOException var28) {
            if(getDevMode()) {
                throw new RenderException(var28);
            }
        } catch (Exception var29) {
            throw new RenderException(var29);
        } finally {
            if(inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException var27) {
                    ;
                }
            }

            if(outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException var26) {
                    ;
                }
            }

        }

    }

    private void processRange(Long[] range) {
        String rangeStr = this.request.getHeader("Range");
        int index = rangeStr.indexOf(44);
        if(index != -1) {
            rangeStr = rangeStr.substring(0, index);
        }

        rangeStr = rangeStr.replace("bytes=", "");
        String[] arr = rangeStr.split("-", 2);
        if(arr.length < 2) {
            throw new RuntimeException("Range error");
        } else {
            long fileLength = this.file.length();

            for(int i = 0; i < range.length; ++i) {
                if(StrKit.notBlank(arr[i])) {
                    range[i] = Long.valueOf(Long.parseLong(arr[i].trim()));
                    if(range[i].longValue() >= fileLength) {
                        range[i] = Long.valueOf(fileLength - 1L);
                    }
                }
            }

            if(range[0] != null && range[1] == null) {
                range[1] = Long.valueOf(fileLength - 1L);
            } else if(range[0] == null && range[1] != null) {
                range[0] = Long.valueOf(fileLength - range[1].longValue());
                range[1] = Long.valueOf(fileLength - 1L);
            }

            if(range[0] == null || range[1] == null || range[0].longValue() > range[1].longValue()) {
                throw new RuntimeException("Range error");
            }
        }
    }
}
