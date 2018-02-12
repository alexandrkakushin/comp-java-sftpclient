package ru.ak.sftpclient;

import com.jcraft.jsch.*;
import ru.ak.sftpclient.model.Connection;
import ru.ak.sftpclient.model.InfoFile;
import ru.ak.sftpclient.model.Response;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import java.util.*;


/**
 * Web-сервис для реализации интерфейса взаимодействия с SFTP-сервером
 * @author akakushin
 */
@WebService(name = "SftpClient", serviceName = "SftpClient", portName = "SftpClientPort")
public class SftpClient {

    private HashMap<UUID, ChannelSftp> channels = new HashMap<>();

    /**
     * Получение канала по уникальному идентификатору
     * @param uuid идентификатор
     * @return SftpChannel
     */
    private ChannelSftp getChannel(UUID uuid) {
        return channels.get(uuid);
    }

    /**
     * Добавление канала в список
     * @param channel ChannelSftp
     * @return UUID
     */
    private UUID addChannel(ChannelSftp channel) {
        UUID uuid = UUID.randomUUID();
        channels.put(uuid, channel);
        return uuid;
    }

    private ChannelSftp openChannel(Connection connection) throws JSchException, SftpException {
        JSch ssh = new JSch();
        Session  session = ssh.getSession(connection.getUser(), connection.getHost(), connection.getPort());

        java.util.Properties config = new java.util.Properties();
        config.put("StrictHostKeyChecking", "no");
        session.setConfig(config);
        session.setPassword(connection.getPassword());
        session.connect();

        Channel channel = session.openChannel("sftp");
        channel.connect();
        return (ChannelSftp) channel;
    }

    /**
     * Выполнение команды
     * @param uuid Соединение
     * @param name имя метода
     * @param args параметры
     * @return Response
     */
    private Response invoke(UUID uuid, String name, Object[] args) {
        Response response = new Response();

        try {
            ChannelSftp channel = getChannel(uuid);
            if (channel != null) {
                if (name.equals("put")) {
                    channel.put((String) args[0], (String) args[1]);

                } else if (name.equals("get")) {
                    channel.get((String) args[0], (String) args[1]);

                } else if (name.equals("rename")) {
                    channel.rename((String) args[0], (String) args[1]);

                } else if (name.equals("rm")) {
                    if ((boolean) args[1]) {
                        channel.rmdir((String) args[0]);
                    } else {
                        channel.rm((String) args[0]);
                    }

                } else if (name.equals("mkdir")) {
                    channel.mkdir((String) args[0]);

                } else if (name.equals("pwd")) {
                    response.setResult(channel.pwd());

                } else if (name.equals("cd")) {
                    channel.cd((String) args[0]);

                } else if (name.equals("ls")) {
                    List<InfoFile> files = new ArrayList<>();
                    Vector<ChannelSftp.LsEntry> ls = channel.ls((String) args[0]);
                    for (ChannelSftp.LsEntry entry : ls) {
                        files.add(new InfoFile(entry.getFilename(), entry.getAttrs().isDir()));
                    }
                    response.setInfoFiles(files);
                }
                response.setError(false);

            } else {
                response.setError(true);
                response.setDescription("Channel is closed");
            }

        } catch (SftpException ex) {
            response.setError(true);
            response.setDescription(ex.getLocalizedMessage());
        }
        return response;
    }


    // Интерфейс взаимодействия

    @WebMethod
    public Response connect(@WebParam(name = "connection") Connection connection) {
        Response response = new Response();
        try {
            ChannelSftp channelSftp = openChannel(connection);
            response.setResult(addChannel(channelSftp));

        }  catch (JSchException | SftpException ex) {
            response.setError(true);
            response.setDescription(ex.getLocalizedMessage());
        }

        return response;
    }

    @WebMethod
    public Response disconnect(String uuid) {
        Response response = new Response();
        ChannelSftp channel = getChannel(UUID.fromString(uuid));
        if (channel != null) {
            try {
                channel.disconnect();
                channel.getSession().disconnect();
                response.setResult(true);

                channels.remove(uuid);

            } catch (JSchException ex) {
                response.setError(true);
                response.setDescription(ex.getLocalizedMessage());
            }
        }
        return response;
    }


    // Методы Web-сервиса

    @WebMethod(operationName = "put")
    public Response put(@WebParam(name = "uuid") String uuid,
                        @WebParam(name = "src") String src,
                        @WebParam(name = "dst") String dst) {

        Object[] args = new Object[2];
        args[0] = src;
        args[1] = dst;

        return invoke(UUID.fromString(uuid), "put", args);
    }

    @WebMethod(operationName = "get")
    public Response get(@WebParam(name = "uuid") String uuid,
                        @WebParam(name = "src") String src,
                        @WebParam(name = "dst") String dst) throws SftpException, JSchException {
        Object[] args = new Object[2];
        args[0] = src;
        args[1] = dst;

        return invoke(UUID.fromString(uuid), "get", args);
    }

    @WebMethod(operationName = "rename")
    public Response rename(@WebParam(name = "uuid") String uuid,
                           @WebParam(name = "oldpath") String oldPath,
                           @WebParam(name = "newPath") String newPath) {
        Object[] args = new Object[2];
        args[0] = oldPath;
        args[1] = newPath;

        return invoke(UUID.fromString(uuid), "rename", args);
    }

    @WebMethod(operationName = "rm")
    public Response rm(@WebParam(name = "uuid") String uuid,
                       @WebParam(name = "path") String path,
                       @WebParam(name = "isDir") boolean isDir) throws SftpException, JSchException {
        Object[] args = new Object[2];
        args[0] = path;
        args[1] = isDir;

        return invoke(UUID.fromString(uuid), "rm", args);
    }

    @WebMethod(operationName = "mkdir")
    public Response mkdir(@WebParam(name = "uuid") String uuid,
                          @WebParam(name = "path") String path) throws SftpException, JSchException {
        Object[] args = new Object[1];
        args[0] = path;

        return invoke(UUID.fromString(uuid), "mkdir", args);
    }

    @WebMethod(operationName = "pwd")
    public Response pwd(@WebParam(name = "uuid") String uuid) throws SftpException, JSchException {
        return invoke(UUID.fromString(uuid), "pwd", null);
    }

    @WebMethod(operationName = "cd")
    public Response cd(@WebParam(name = "uuid") String uuid,
                       @WebParam(name = "path") String path) throws SftpException, JSchException {
        Object[] args = new Object[1];
        args[0] = path;

        return invoke(UUID.fromString(uuid), "cd", args);
    }

    @WebMethod(operationName = "ls")
    public Response ls(@WebParam(name = "uuid") String uuid,
                       @WebParam(name = "path") String path) throws SftpException, JSchException {
        Object[] args = new Object[1];
        args[0] = path;

        return invoke(UUID.fromString(uuid), "ls", args);
    }

}
