/*
 * SCIM-Client is available under the MIT License (2008). See http://opensource.org/licenses/MIT for full text.
 *
 * Copyright (c) 2017, Gluu
 */
package gluu.scim2.client;

import gluu.scim2.client.factory.ScimClientFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.gluu.oxtrust.model.scim2.*;
import org.gluu.oxtrust.model.scim2.user.UserResource;
import org.gluu.oxtrust.ws.rs.scim2.IUserWebService;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import javax.ws.rs.core.Response;

import java.util.Map;
import java.util.Set;

import static javax.ws.rs.core.Response.Status.OK;
import static org.gluu.oxtrust.model.scim2.Constants.USER_EXT_SCHEMA_ID;
import static org.testng.Assert.*;

/**
 * Created by jgomer on 2017-09-14.
 */
public class SampleTest extends BaseTest {

    private Logger logger = LogManager.getLogger(getClass());

    @Test
    @Parameters ({"domainURL", "umaAatClientId", "umaAatClientJksPath", "umaAatClientJksPassword", "umaAatClientKeyId"})
    public void smallerClient(String domainURL, String umaAatClientId, String umaAatClientJksPath, String umaAatClientJksPassword,
                              String umaAatClientKeyId) throws Exception {

        IUserWebService myclient = ScimClientFactory.getClient(IUserWebService.class, domainURL, umaAatClientId,
                umaAatClientJksPath, umaAatClientJksPassword, umaAatClientKeyId);

        SearchRequest sr=new SearchRequest();
        sr.setFilter("userName eq \"n0v4c4n3\"");

        Response response = myclient.searchUsersPost(sr);
        assertEquals(response.getStatus(), OK.getStatusCode());

        UserResource u = (UserResource) response.readEntity(ListResponse.class).getResources().get(0);
        logger.debug("Hello {}!", u.getDisplayName());

        Set<String> schemaNames = u.getSchemas();
        logger.debug("Searching schemas");
        for (String schema : schemaNames) {
            logger.debug("Attributes: " + schema);
        }

        CustomAttributes custAttrs1 = u.getCustomAttributes(USER_EXT_SCHEMA_ID);
        Set<String> attrNames = custAttrs1.getAttributeNames();
        logger.debug("Searching attributes");
        for (String name : attrNames) {
            logger.debug("Attributes: " + name);
        }

        logger.debug("Searching attributes map");
        Map<String, Object> map = u.getCustomAttributes();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            logger.debug(entry.getKey() + ":" + entry.getValue().toString());
        }
    }

    //@Test
    @Parameters({"domainURL", "OIDCMetadataUrl"})
    //This test showcases test mode usage (not typical UMA protection mode). Run only under such condition
    public void testModeTest(String domain, String url) throws Exception{

        IUserWebService myclient = ScimClientFactory.getTestClient(IUserWebService.class, domain, url);

        SearchRequest sr=new SearchRequest();
        sr.setFilter("pairwiseIdentitifers pr");
        sr.setSortBy("meta.lastModified");

        Response response = myclient.searchUsersPost(sr);
        assertEquals(response.getStatus(), OK.getStatusCode());

        logger.debug("There are {} users with PPIDs associated", response.readEntity(ListResponse.class).getResources().size());

    }

}
