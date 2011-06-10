
package org.ebayopensource.turmeric.uat.v1.services.messageservice.gen;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.concurrent.Future;
import javax.xml.ws.AsyncHandler;
import javax.xml.ws.Response;
import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceException;
import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceRuntimeException;
import org.ebayopensource.turmeric.runtime.common.types.Cookie;
import org.ebayopensource.turmeric.runtime.common.types.SOAHeaders;
import org.ebayopensource.turmeric.runtime.sif.service.Service;
import org.ebayopensource.turmeric.runtime.sif.service.ServiceFactory;
import org.ebayopensource.turmeric.runtime.sif.service.ServiceInvokerOptions;
import org.ebayopensource.turmeric.uat.v1.services.GetMessagesRequest;
import org.ebayopensource.turmeric.uat.v1.services.GetMessagesResponse;
import org.ebayopensource.turmeric.uat.v1.services.GetVersionRequest;
import org.ebayopensource.turmeric.uat.v1.services.GetVersionResponse;
import org.ebayopensource.turmeric.uat.v1.services.SendMessageRequest;
import org.ebayopensource.turmeric.uat.v1.services.SendMessageResponse;
import org.ebayopensource.turmeric.uat.v1.services.messageservice.impl.AsyncUatMessageServiceV1;


/**
 * Note : Generated file, any changes will be lost upon regeneration.
 * This class is not thread safe
 * 
 */
public class SharedUat2MessageServiceV1Consumer
    implements AsyncUatMessageServiceV1
{

    private URL m_serviceLocation = null;
    private final static String SVC_ADMIN_NAME = "Uat2MessageServiceV1";
    private String m_clientName;
    private String m_environment = "production";
    private AsyncUatMessageServiceV1 m_proxy = null;
    private String m_authToken = null;
    private Cookie[] m_cookies;
    private Service m_service = null;

    public SharedUat2MessageServiceV1Consumer(String clientName)
        throws ServiceException
    {
        if (clientName == null) {
            throw new ServiceException("clientName can not be null");
        }
        m_clientName = clientName;
    }

    public SharedUat2MessageServiceV1Consumer(String clientName, String environment)
        throws ServiceException
    {
        if (environment == null) {
            throw new ServiceException("environment can not be null");
        }
        if (clientName == null) {
            throw new ServiceException("clientName can not be null");
        }
        m_clientName = clientName;
        m_environment = environment;
    }

    /**
     * Use this method to initialize ConsumerApp after creating a Consumer instance
     * 
     */
    public void init()
        throws ServiceException
    {
        getService();
    }

    protected void setServiceLocation(String serviceLocation)
        throws MalformedURLException
    {
        m_serviceLocation = new URL(serviceLocation);
        if (m_service!= null) {
            m_service.setServiceLocation(m_serviceLocation);
        }
    }

    private void setUserProvidedSecurityCredentials(Service service) {
        if (m_authToken!= null) {
            service.setSessionTransportHeader(SOAHeaders.AUTH_TOKEN, m_authToken);
        }
        if (m_cookies!= null) {
            for (int i = 0; (i<m_cookies.length); i ++) {
                service.setCookie(m_cookies[i]);
            }
        }
    }

    /**
     * Use this method to set User Credentials (Token) 
     * 
     */
    protected void setAuthToken(String authToken) {
        m_authToken = authToken;
    }

    /**
     * Use this method to set User Credentials (Cookie)
     * 
     */
    protected void setCookies(Cookie[] cookies) {
        m_cookies = cookies;
    }

    /**
     * Use this method to get the Invoker Options on the Service and set them to user-preferences
     * 
     */
    public ServiceInvokerOptions getServiceInvokerOptions()
        throws ServiceException
    {
        m_service = getService();
        return m_service.getInvokerOptions();
    }

    protected AsyncUatMessageServiceV1 getProxy()
        throws ServiceException
    {
        m_service = getService();
        m_proxy = m_service.getProxy();
        return m_proxy;
    }

    /**
     * Method returns an instance of Service which has been initilized for this Consumer
     * 
     */
    public Service getService()
        throws ServiceException
    {
        if (m_service == null) {
            m_service = ServiceFactory.create(SVC_ADMIN_NAME, m_environment, m_clientName, m_serviceLocation);
        }
        setUserProvidedSecurityCredentials(m_service);
        return m_service;
    }

    public Future<?> getMessagesAsync(GetMessagesRequest param0, AsyncHandler<GetMessagesResponse> param1) {
        Future<?> result = null;
        try {
            m_proxy = getProxy();
        } catch (ServiceException serviceException) {
            throw ServiceRuntimeException.wrap(serviceException);
        }
        result = m_proxy.getMessagesAsync(param0, param1);
        return result;
    }

    public Response<GetMessagesResponse> getMessagesAsync(GetMessagesRequest param0) {
        Response<GetMessagesResponse> result = null;
        try {
            m_proxy = getProxy();
        } catch (ServiceException serviceException) {
            throw ServiceRuntimeException.wrap(serviceException);
        }
        result = m_proxy.getMessagesAsync(param0);
        return result;
    }

    public Future<?> getVersionAsync(GetVersionRequest param0, AsyncHandler<GetVersionResponse> param1) {
        Future<?> result = null;
        try {
            m_proxy = getProxy();
        } catch (ServiceException serviceException) {
            throw ServiceRuntimeException.wrap(serviceException);
        }
        result = m_proxy.getVersionAsync(param0, param1);
        return result;
    }

    public Response<GetVersionResponse> getVersionAsync(GetVersionRequest param0) {
        Response<GetVersionResponse> result = null;
        try {
            m_proxy = getProxy();
        } catch (ServiceException serviceException) {
            throw ServiceRuntimeException.wrap(serviceException);
        }
        result = m_proxy.getVersionAsync(param0);
        return result;
    }

    public Future<?> sendMessageAsync(SendMessageRequest param0, AsyncHandler<SendMessageResponse> param1) {
        Future<?> result = null;
        try {
            m_proxy = getProxy();
        } catch (ServiceException serviceException) {
            throw ServiceRuntimeException.wrap(serviceException);
        }
        result = m_proxy.sendMessageAsync(param0, param1);
        return result;
    }

    public Response<SendMessageResponse> sendMessageAsync(SendMessageRequest param0) {
        Response<SendMessageResponse> result = null;
        try {
            m_proxy = getProxy();
        } catch (ServiceException serviceException) {
            throw ServiceRuntimeException.wrap(serviceException);
        }
        result = m_proxy.sendMessageAsync(param0);
        return result;
    }

    public List<Response<?>> poll(boolean param0, boolean param1)
        throws InterruptedException
    {
        List<Response<?>> result = null;
        try {
            m_proxy = getProxy();
        } catch (ServiceException serviceException) {
            throw ServiceRuntimeException.wrap(serviceException);
        }
        result = m_proxy.poll(param0, param1);
        return result;
    }

    public GetMessagesResponse getMessages(GetMessagesRequest param0) {
        GetMessagesResponse result = null;
        try {
            m_proxy = getProxy();
        } catch (ServiceException serviceException) {
            throw ServiceRuntimeException.wrap(serviceException);
        }
        result = m_proxy.getMessages(param0);
        return result;
    }

    public GetVersionResponse getVersion(GetVersionRequest param0) {
        GetVersionResponse result = null;
        try {
            m_proxy = getProxy();
        } catch (ServiceException serviceException) {
            throw ServiceRuntimeException.wrap(serviceException);
        }
        result = m_proxy.getVersion(param0);
        return result;
    }

    public SendMessageResponse sendMessage(SendMessageRequest param0) {
        SendMessageResponse result = null;
        try {
            m_proxy = getProxy();
        } catch (ServiceException serviceException) {
            throw ServiceRuntimeException.wrap(serviceException);
        }
        result = m_proxy.sendMessage(param0);
        return result;
    }

}
