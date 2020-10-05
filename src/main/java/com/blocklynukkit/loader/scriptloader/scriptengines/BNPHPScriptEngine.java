package com.blocklynukkit.loader.scriptloader.scriptengines;

import com.blocklynukkit.loader.Utils;
import com.blocklynukkit.loader.script.window.Simple;
import com.caucho.quercus.QuercusContext;
import com.caucho.quercus.QuercusEngine;
import com.caucho.quercus.QuercusExitException;
import com.caucho.quercus.env.*;
import com.caucho.quercus.lib.*;
import com.caucho.quercus.lib.curl.CurlModule;
import com.caucho.quercus.lib.date.DateModule;
import com.caucho.quercus.lib.file.FileModule;
import com.caucho.quercus.lib.file.SocketModule;
import com.caucho.quercus.lib.file.StreamModule;
import com.caucho.quercus.lib.filter.FilterModule;
import com.caucho.quercus.lib.gettext.GettextModule;
import com.caucho.quercus.lib.i18n.MbstringModule;
import com.caucho.quercus.lib.image.ImageModule;
import com.caucho.quercus.lib.jms.JMSModule;
import com.caucho.quercus.lib.json.JsonModule;
import com.caucho.quercus.lib.mcrypt.McryptModule;
import com.caucho.quercus.lib.pdf.PDFModule;
import com.caucho.quercus.lib.regexp.RegexpModule;
import com.caucho.quercus.lib.session.SessionModule;
import com.caucho.quercus.lib.simplexml.SimpleXMLModule;
import com.caucho.quercus.lib.string.StringModule;
import com.caucho.quercus.lib.xml.XMLWriterModule;
import com.caucho.quercus.lib.xml.XmlModule;
import com.caucho.quercus.lib.zip.ZipModule;
import com.caucho.quercus.lib.zlib.ZlibModule;
import com.caucho.quercus.page.InterpretedPage;
import com.caucho.quercus.page.QuercusPage;
import com.caucho.quercus.parser.QuercusParser;
import com.caucho.quercus.program.QuercusProgram;
import com.caucho.quercus.script.EncoderStream;
import com.caucho.quercus.script.QuercusScriptEngine;
import com.caucho.quercus.script.QuercusScriptEngineFactory;
import com.caucho.quercus.servlet.api.QuercusHttpServletRequest;
import com.caucho.quercus.servlet.api.QuercusHttpServletResponse;
import com.caucho.vfs.*;

import javax.script.*;
import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BNPHPScriptEngine extends QuercusScriptEngine implements Invocable {
    public Env env;
    public QuercusContext quercus;

    public BNPHPScriptEngine(){
        super(new QuercusScriptEngineFactory(),true);
    }

    @Override
    public QuercusContext getQuercus() {
        if (this.quercus == null) {
            QuercusContext _quercus = new QuercusContext();
            _quercus.setScriptEncoding(this.getScriptEncoding());
            _quercus.setUnicodeSemantics(this.isUnicodeSemantics());
            _quercus.addInitModule(new ApcModule());
            _quercus.addInitModule(new ArrayModule());
            _quercus.addInitModule(new Array2Module());
            _quercus.addInitModule(new BcmathModule());
            _quercus.addInitModule(new ClassesModule());
            _quercus.addInitModule(new CoreModule());
            _quercus.addInitModule(new CtypeModule());
            _quercus.addInitModule(new ExifModule());
            _quercus.addInitModule(new FunctionModule());
            _quercus.addInitModule(new HashModule());
            _quercus.addInitModule(new JavaModule());
            _quercus.addInitModule(new MathModule());
            _quercus.addInitModule(new MhashModule());
            _quercus.addInitModule(new MiscModule());
            _quercus.addInitModule(new NetworkModule());
            _quercus.addInitModule(new OptionsModule());
            _quercus.addInitModule(new OutputModule());
            _quercus.addInitModule(new QuercusModule());
            _quercus.addInitModule(new TokenModule());
            _quercus.addInitModule(new UrlModule());
            _quercus.addInitModule(new VariableModule());
            _quercus.addInitModule(new CurlModule());
            _quercus.addInitModule(new DateModule());
            _quercus.addInitModule(new FileModule());
            _quercus.addInitModule(new SocketModule());
            _quercus.addInitModule(new StreamModule());
            _quercus.addInitModule(new FilterModule());
            _quercus.addInitModule(new GettextModule());
            _quercus.addInitModule(new MbstringModule());
            _quercus.addInitModule(new ImageModule());
            _quercus.addInitModule(new JMSModule());
            _quercus.addInitModule(new JsonModule());
            _quercus.addInitModule(new McryptModule());
            _quercus.addInitModule(new PDFModule());
            _quercus.addInitModule(new RegexpModule());
            _quercus.addInitModule(new SessionModule());
            _quercus.addInitModule(new SimpleXMLModule());
            _quercus.addInitModule(new StringModule());
            _quercus.addInitModule(new XmlModule());
            _quercus.addInitModule(new XMLWriterModule());
            _quercus.addInitModule(new ZipModule());
            _quercus.addInitModule(new ZlibModule());
            _quercus.init();
            _quercus.start();
            return _quercus;
        }
        return this.quercus;
    }

    @Override
    public Object invokeMethod(Object thiz, String name, Object... args) throws ScriptException, NoSuchMethodException {
        return null;
    }

    @Override
    public Object invokeFunction(String name, Object... args) throws ScriptException, NoSuchMethodException {
        env.setScriptContext(this.getContext());
        if(args.length==0){
            env.findFunction((StringValue)StringValue.create(name)).call(env);
        }else {
            Value[] values = new Value[args.length];
            for(int i=0;i<args.length;i++){
                values[i] = env.wrapJava(args[i]);
            }
            return env.findFunction(
                    (StringValue)StringValue.create(name)).call(
                            env, values);
        }
        return null;
    }

    @Override
    public Object get(String key){
        Value value = env.getValue((StringValue)StringValue.create(key));
        if(value.isEmpty()){
            value = env.findFunction((StringValue)StringValue.create(key));
        }
        return value==null?null:(value.isEmpty()?null:value);
    }

    @Override
    public <T> T getInterface(Class<T> clasz) {
        return null;
    }

    @Override
    public <T> T getInterface(Object thiz, Class<T> clasz) {
        return null;
    }

    @Override
    public Object eval(Reader script, ScriptContext cxt) throws ScriptException {
        QuercusContext quercus = this.getQuercus();
        env = null;

        Value value;
        try {
            QuercusProgram program;
            if (this.isUnicodeSemantics()) {
                program = QuercusParser.parse(quercus, (Path)null, script);
            } else {
                InputStream is = EncoderStream.open(script, quercus.getScriptEncoding());
                ReadStream rs = new ReadStream(new VfsStream(is, (OutputStream)null));
                program = QuercusParser.parse(quercus, (Path)null, rs);
            }

            Writer writer = cxt.getWriter();
            Object out;
            if (writer != null) {
                WriterStreamImpl s = new WriterStreamImpl();
                s.setWriter(writer);
                WriteStream os = new WriteStream(s);
                os.setNewlineString("\n");
                String outputEncoding = quercus.getOutputEncoding();
                if (outputEncoding == null) {
                    outputEncoding = "utf-8";
                }

                try {
                    os.setEncoding(outputEncoding);
                } catch (Exception var20) {
                    Logger.getLogger(QuercusScriptEngine.class.getName()).log(Level.FINE, var20.getMessage(), var20);
                }

                out = os;
            } else {
                out = new NullWriteStream();
            }

            QuercusPage page = new InterpretedPage(program);
            env = new Env(quercus, page, (WriteStream)out, (QuercusHttpServletRequest)null, (QuercusHttpServletResponse)null);
            env.setScriptContext(cxt);
            env.start();
            Value result = null;

            try {
                value = program.execute(env);
                if (value != null) {
                    result = value;
                }
            } catch (QuercusExitException var19) {
            }

            ((WriteStream)out).flushBuffer();
            ((WriteStream)out).free();
            writer.flush();
            value = result;
        } catch (RuntimeException var21) {
            throw var21;
        } catch (Exception var22) {
            throw new ScriptException(var22);
        } catch (Throwable var23) {
            throw new RuntimeException(var23);
        }

        return value;
    }
//    @Override
//    public Object eval(String code) throws ScriptException{
//        env = new Env(this.getQuercus());
//        env.setRuntimeEncoding("utf-8");
//        env.addInitModule("ApcModule",new ApcModule());
//        env.addInitModule("ArrayModule",new ArrayModule());
//        env.addInitModule("Array2Module",new Array2Module());
//        env.addInitModule("BcmathModule",new BcmathModule());
//        env.addInitModule("ClassesModule",new ClassesModule());
//        env.addInitModule("CoreModule",new CoreModule());
//        env.addInitModule("CtypeModule",new CtypeModule());
//        env.addInitModule("ExifModule",new ExifModule());
//        env.addInitModule("FunctionModule",new FunctionModule());
//        env.addInitModule("HashModule",new HashModule());
//        env.addInitModule("JavaModule",new JavaModule());
//        env.addInitModule("MathModule",new MathModule());
//        env.addInitModule("MhashModule",new MhashModule());
//        env.addInitModule("MiscModule",new MiscModule());
//        env.addInitModule("NetworkModule",new NetworkModule());
//        env.addInitModule("OptionsModule",new OptionsModule());
//        env.addInitModule("OutputModule",new OutputModule());
//        env.addInitModule("QuercusModule",new QuercusModule());
//        env.addInitModule("TokenModule",new TokenModule());
//        env.addInitModule("UrlModule",new UrlModule());
//        env.addInitModule("VariableModule",new VariableModule());
//        env.addInitModule("CurlModule",new CurlModule());
//        env.addInitModule("DateModule",new DateModule());
//        env.addInitModule("FileModule",new FileModule());
//        env.addInitModule("SocketModule",new SocketModule());
//        env.addInitModule("StreamModule",new StreamModule());
//        env.addInitModule("FilterModule",new FilterModule());
//        env.addInitModule("GettextModule",new GettextModule());
//        env.addInitModule("MbstringModule",new MbstringModule());
//        env.addInitModule("ImageModule",new ImageModule());
//        env.addInitModule("JMSModule",new JMSModule());
//        env.addInitModule("JsonModule",new JsonModule());
//        env.addInitModule("McryptModule",new McryptModule());
//        env.addInitModule("PDFModule",new PDFModule());
//        env.addInitModule("RegexpModule",new RegexpModule());
//        env.addInitModule("SessionModule",new SessionModule());
//        env.addInitModule("SimpleXMLModule",new SimpleXMLModule());
//        env.addInitModule("StringModule",new StringModule());
//        env.addInitModule("XmlModule",new XmlModule());
//        env.addInitModule("XMLWriterModule",new XMLWriterModule());
//        env.addInitModule("ZipModule",new ZipModule());
//        env.addInitModule("ZlibModule",new ZlibModule());
//        env.getModuleContext().init();
//        env.setScriptContext(this.getContext());
//        env.start();
//        try {
//            return env.evalCode((StringValue)StringValue.create(Utils.replaceLast(code,"?>","").replaceFirst("<\\?php","")));
//        } catch (IOException e) {
//            e.printStackTrace();
//            return null;
//        }
//    }

}
