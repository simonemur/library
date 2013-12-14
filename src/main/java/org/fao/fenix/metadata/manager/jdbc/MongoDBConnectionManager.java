/**
 *
 * FENIX (Food security and Early warning Network and Information Exchange)
 *
 * Copyright (c) 2011, by FAO of UN under the EC-FAO Food Security
 Information for Action Programme
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 */
package org.fao.fenix.metadata.manager.jdbc;

import com.mongodb.Mongo;

import java.net.UnknownHostException;

/**
 * @author <a href="mailto:guido.barbaglia@fao.org">Guido Barbaglia</a>
 * @author <a href="mailto:guido.barbaglia@gmail.com">Guido Barbaglia</a>
 */
public class MongoDBConnectionManager {

    private final static String HOST = "localhost";

    private final static int PORT = 27017;

    private static MongoDBConnectionManager instance = null;

    private static Mongo mongo;

    protected MongoDBConnectionManager() {

    }

    public static MongoDBConnectionManager getInstance() {
        if(instance == null)
            instance = new MongoDBConnectionManager();
        return instance;
    }

    public static Mongo getMongo() throws UnknownHostException {
        if(mongo == null)
            mongo = new Mongo(HOST, PORT);
        return mongo;
    }

}