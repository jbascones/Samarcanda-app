package com.jorgebascones.samarcanda;

/**
 * Created by jorgebascones on 13/3/18.
 */

public class Herramientas {
    String confJson = "{" +
            "  \"menus\" : [" +
            "    {" +
            "      \"index\" : \"0\"," +
            "      \"expandable\" : true," +
            "      \"text\" : \"Día\"," +
            "      \"child\" : [" +
            "        {" +
            "          \"index\" : \"00\"," +
            "          \"expandable\" : false," +
            "          \"text\" : \"Hoy\"" +
            "        }," +
            "        {" +
            "          \"index\" : \"01\"," +
            "          \"expandable\" : false," +
            "          \"text\" : \"Mañana\"" +
            "        }"+
            "      ]"+
            "    }," +
            "    {" +
            "      \"index\" : \"1\"," +
            "      \"expandable\" : true," +
            "      \"text\" : \"Estado\"," +
            "      \"child\" : [" +
            "        {" +
            "          \"index\" : \"10\"," +
            "          \"expandable\" : false," +
            "          \"text\" : \"En proceso\"" +
            "        }," +
            "        {" +
            "          \"index\" : \"11\"," +
            "          \"expandable\" : false," +
            "          \"text\" : \"Reserva confirmada\"," +
            "          \"child\" : [" +
            "            {" +
            "              \"index\" : \"110\"," +
            "              \"expandable\" : false," +
            "              \"text\" : \"Child Menu 210\"" +
            "            }," +
            "            {" +
            "              \"index\" : \"111\"," +
            "              \"expandable\" : false," +
            "              \"text\" : \"Child Menu 211\"" +
            "            }" +
            "          ]" +
            "        }" +
            "      ]" +
            "    }" +
            "  ]" +
            "}";

    String confJsonCliente = "{" +
            "  \"menus\" : [" +
            "    {" +
            "      \"index\" : \"0\"," +
            "      \"expandable\" : true," +
            "      \"text\" : \"Reservado hasta\"," +
            "      \"child\" : [" +
            "        {" +
            "          \"index\" : \"00\"," +
            "          \"expandable\" : false," +
            "          \"text\" : \"Hoy\"" +
            "        }," +
            "        {" +
            "          \"index\" : \"01\"," +
            "          \"expandable\" : false," +
            "          \"text\" : \"Mañana\"" +
            "        }"+
            "      ]"+
            "    }," +
            "    {" +
            "      \"index\" : \"1\"," +
            "      \"expandable\" : true," +
            "      \"text\" : \"Estado\"," +
            "      \"child\" : [" +
            "        {" +
            "          \"index\" : \"10\"," +
            "          \"expandable\" : false," +
            "          \"text\" : \"Pendiente\"" +
            "        }," +
            "        {" +
            "          \"index\" : \"11\"," +
            "          \"expandable\" : false," +
            "          \"text\" : \"Ya disponible\"," +
            "          \"child\" : [" +
            "            {" +
            "              \"index\" : \"110\"," +
            "              \"expandable\" : false," +
            "              \"text\" : \"Child Menu 210\"" +
            "            }," +
            "            {" +
            "              \"index\" : \"111\"," +
            "              \"expandable\" : false," +
            "              \"text\" : \"Child Menu 211\"" +
            "            }" +
            "          ]" +
            "        }" +
            "      ]" +
            "    }" +
            "  ]" +
            "}";

    public String getConfJson(){
        return confJson;
    }

    public String getConfJsonCliente(){return confJsonCliente;}

}
