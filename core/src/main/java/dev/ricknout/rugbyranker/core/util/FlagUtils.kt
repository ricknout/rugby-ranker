package dev.ricknout.rugbyranker.core.util

object FlagUtils {

    fun getFlagEmojiForTeamAbbreviation(teamAbbreviation: String?) = when (teamAbbreviation) {

        "ALG" -> "\uD83C\uDDE9\uD83C\uDDFF" // Algeria
        "ASM", "ASA" -> "\uD83C\uDDE6\uD83C\uDDF8" // American Samoa
        "AND" -> "\uD83C\uDDE6\uD83C\uDDE9" // Andorra
        "ARG", "ARA", "ArJ" -> "\uD83C\uDDE6\uD83C\uDDF7" // Argentina
        "ARM" -> "\uD83C\uDDE6\uD83C\uDDF2" // Armenia
        "AUS", "AUA", "AUN", "EAU" -> "\uD83C\uDDE6\uD83C\uDDFA" // Australia
        "AUT" -> "\uD83C\uDDE6\uD83C\uDDF9" // Austria
        "AZE" -> "\uD83C\uDDE6\uD83C\uDDFF" // Azerbaijan

        "BHS", "BAH" -> "\uD83C\uDDE7\uD83C\uDDF8" // Bahamas
        "BRN" -> "\uD83C\uDDE7\uD83C\uDDED" // Bahrain
        "BRB", "BAR" -> "\uD83C\uDDE7\uD83C\uDDE7" // Barbados
        "BLR" -> "\uD83C\uDDE7\uD83C\uDDFE" // Belarus
        "BEL" -> "\uD83C\uDDE7\uD83C\uDDEA" // Belgium
        "BEN" -> "\uD83C\uDDE7\uD83C\uDDEF" // Benin
        "BER" -> "\uD83C\uDDE7\uD83C\uDDF2" // Bermuda
        "BIH" -> "\uD83C\uDDE7\uD83C\uDDE6" // Bosnia and Herzegovina
        "BWA", "BOT" -> "\uD83C\uDDE7\uD83C\uDDFC" // Botswana
        "BRA" -> "\uD83C\uDDE7\uD83C\uDDF7" // Brazil
        "BIL" -> "\uD83E\uDD81" // British & Irish Lions (using a lion)
        "IVB" -> "\uD83C\uDDFB\uD83C\uDDEC" // British Virgin Islands
        "BRU" -> "\uD83C\uDDE7\uD83C\uDDF3" // Brunei
        "BUL" -> "\uD83C\uDDE7\uD83C\uDDEC" // Bulgaria
        "BUR" -> "\uD83C\uDDE7\uD83C\uDDEB" // Burkina Faso
        "BDI" -> "\uD83C\uDDE7\uD83C\uDDEE" // Burundi

        "CAM" -> "\uD83C\uDDF0\uD83C\uDDED" // Cambodia
        "CMR" -> "\uD83C\uDDE8\uD83C\uDDF2" // Cameroon
        "CAN" -> "\uD83C\uDDE8\uD83C\uDDE6" // Canada
        "CAY" -> "\uD83C\uDDF0\uD83C\uDDFE" // Cayman Islands
        "CHA" -> "\uD83C\uDDF9\uD83C\uDDE9" // Chad
        "CHL", "CHI" -> "\uD83C\uDDE8\uD83C\uDDF1" // Chile
        "CHN" -> "\uD83C\uDDE8\uD83C\uDDF3" // China
        "TPE" -> "\uD83C\uDDF9\uD83C\uDDFC" // Chinese Taipei
        "COL" -> "\uD83C\uDDE8\uD83C\uDDF4" // Colombia
        "CGO" -> "\uD83C\uDDE8\uD83C\uDDEC" // Congo
        "COK" -> "\uD83C\uDDE8\uD83C\uDDF0" // Cook Islands
        "CTR", "CRC" -> "\uD83C\uDDE8\uD83C\uDDF7" // Costa Rica
        "CIV" -> "\uD83C\uDDE8\uD83C\uDDEE" // Cote D'Ivoire
        "CRO" -> "\uD83C\uDDED\uD83C\uDDF7" // Croatia
        "CUW" -> "\uD83C\uDDE8\uD83C\uDDFC" // Curacao
        "CYP" -> "\uD83C\uDDE8\uD83C\uDDFE" // Cyprus
        "CZE" -> "\uD83C\uDDE8\uD83C\uDDFF" // Czech Republic, Czechia

        "COD" -> "\uD83C\uDDE8\uD83C\uDDE9" // Democratic Republic of Congo
        "DEN" -> "\uD83C\uDDE9\uD83C\uDDF0" // Denmark

        "ECU" -> "\uD83C\uDDEA\uD83C\uDDE8" // Ecuador
        "EGY" -> "\uD83C\uDDEA\uD83C\uDDEC" // Egypt
        "ESA" -> "\uD83C\uDDF8\uD83C\uDDFB" // El Salvador
        "ENG", "ENA", "ENB", "ECO", "EEN" -> "\uD83C\uDFF4\uDB40\uDC67\uDB40\uDC62\uDB40\uDC65\uDB40\uDC6E\uDB40\uDC67\uDB40\uDC7F" // England
        "EST" -> "\uD83C\uDDEA\uD83C\uDDEA" // Estonia

        "FJI", "FIJ" -> "\uD83C\uDDEB\uD83C\uDDEF" // Fiji
        "FIN" -> "\uD83C\uDDEB\uD83C\uDDEE" // Finland
        "FRA", "FAM", "FAr", "FRM" -> "\uD83C\uDDEB\uD83C\uDDF7" // France

        "GEO" -> "\uD83C\uDDEC\uD83C\uDDEA" // Georgia
        "GER", "GEB" -> "\uD83C\uDDE9\uD83C\uDDEA" // Germany
        "GHA" -> "\uD83C\uDDEC\uD83C\uDDED" // Ghana
        "GBR", "BEF" -> "\uD83C\uDDEC\uD83C\uDDE7" // Great Britain
        "HEL", "GRE" -> "\uD83C\uDDEC\uD83C\uDDF7" // Greece
        "GLP" -> "\uD83C\uDDEC\uD83C\uDDF5" // Guadeloupe
        "GUM" -> "\uD83C\uDDEC\uD83C\uDDFA" // Guam
        "GUA" -> "\uD83C\uDDEC\uD83C\uDDF9" // Guatemala
        "GUY" -> "\uD83C\uDDEC\uD83C\uDDFE" // Guyana

        "HKG" -> "\uD83C\uDDED\uD83C\uDDF0" // Hong Kong
        "HUN" -> "\uD83C\uDDED\uD83C\uDDFA" // Hungary

        "IND" -> "\uD83C\uDDEE\uD83C\uDDF3" // India
        "IDO", "INA" -> "\uD83C\uDDEE\uD83C\uDDE9" // Indonesia
        "IRI" -> "\uD83C\uDDEE\uD83C\uDDF7" // Iran
        "IRE", "IA", "EIR", "IST", "I25" -> "\u2618\uFE0F" // Ireland (using a shamrock for IRFU)
        "IOM" -> "\uD83C\uDDEE\uD83C\uDDF2" // Isle of Man
        "ISR" -> "\uD83C\uDDEE\uD83C\uDDF1" // Israel
        "ITA", "ItA" -> "\uD83C\uDDEE\uD83C\uDDF9" // Italy

        "JAM" -> "\uD83C\uDDEF\uD83C\uDDF2" // Jamaica
        "JPN" -> "\uD83C\uDDEF\uD83C\uDDF5" // Japan
        "JRD" -> "\uD83C\uDDEF\uD83C\uDDEA" // Jersey
        "JOR" -> "\uD83C\uDDEF\uD83C\uDDF4" // Jordan

        "KAZ" -> "\uD83C\uDDF0\uD83C\uDDFF" // Kazakhstan
        "KEN" -> "\uD83C\uDDF0\uD83C\uDDEA" // Kenya
        "KOR" -> "\uD83C\uDDF0\uD83C\uDDF7" // Korea
        "KGZ" -> "\uD83C\uDDF0\uD83C\uDDEC" // Kyrgyzstan

        "LAO" -> "\uD83C\uDDF1\uD83C\uDDE6" // Laos
        "LAT" -> "\uD83C\uDDF1\uD83C\uDDFB" // Latvia
        "LIB" -> "\uD83C\uDDF1\uD83C\uDDE7" // Lebanon
        "LES" -> "\uD83C\uDDF1\uD83C\uDDF8" // Lesotho
        "LBY" -> "\uD83C\uDDF1\uD83C\uDDFE" // Libya
        "LTU" -> "\uD83C\uDDF1\uD83C\uDDF9" // Lithuania
        "LUX" -> "\uD83C\uDDF1\uD83C\uDDFA" // Luxembourg

        "MAC" -> "\uD83C\uDDF2\uD83C\uDDF4" // Macau
        "MAD" -> "\uD83C\uDDF2\uD83C\uDDEC" // Madagascar
        "MAW" -> "\uD83C\uDDF2\uD83C\uDDFC" // Malawi
        "MAS" -> "\uD83C\uDDF2\uD83C\uDDFE" // Malaysia
        "MLI" -> "\uD83C\uDDF2\uD83C\uDDF1" // Mali
        "MLT" -> "\uD83C\uDDF2\uD83C\uDDF9" // Malta
        "MTO" -> "\uD83C\uDDF2\uD83C\uDDF6" // Martinique
        "MTN" -> "\uD83C\uDDF2\uD83C\uDDF7" // Mauritania
        "MUS", "MRI" -> "\uD83C\uDDF2\uD83C\uDDFA" // Mauritius
        "MYT" -> "\uD83C\uDDFE\uD83C\uDDF9" // Mayotte
        "MEX" -> "\uD83C\uDDF2\uD83C\uDDFD" // Mexico
        "MDA" -> "\uD83C\uDDF2\uD83C\uDDE9" // Moldova
        "MON" -> "\uD83C\uDDF2\uD83C\uDDE8" // Monaco
        "MGL" -> "\uD83C\uDDF2\uD83C\uDDF3" // Mongolia
        "MNE", "PXV" -> "\uD83C\uDDF2\uD83C\uDDEA" // Montenegro
        "MAR" -> "\uD83C\uDDF2\uD83C\uDDE6" // Morocco

        "NAM" -> "\uD83C\uDDF3\uD83C\uDDE6" // Namibia
        "NRU" -> "\uD83C\uDDF3\uD83C\uDDF7" // Nauru
        "NIG" -> "\uD83C\uDDF3\uD83C\uDDEA" // Niger
        "NGA", "NGR" -> "\uD83C\uDDF3\uD83C\uDDEC" // Nigeria
        "NIU" -> "\uD83C\uDDF3\uD83C\uDDFA" // Niue Island
        "NCL" -> "\uD83C\uDDF3\uD83C\uDDE8" // New Caledonia
        "NZL", "MAB", "NZH", "NZA", "NZC", "JAB", "NZU", "N23", "NZD", "CAB" -> "\uD83C\uDDF3\uD83C\uDDFF" // New Zealand
        "NED" -> "\uD83C\uDDF3\uD83C\uDDF1" // Netherlands
        "NOR" -> "\uD83C\uDDF3\uD83C\uDDF4" // Norway

        "PAK" -> "\uD83C\uDDF5\uD83C\uDDF0" // Pakistan
        "PAN" -> "\uD83C\uDDF5\uD83C\uDDE6" // Panama
        "PNG" -> "\uD83C\uDDF5\uD83C\uDDEC" // Papua New Guinea
        "PAR" -> "\uD83C\uDDF5\uD83C\uDDFE" // Paraguay
        "PER" -> "\uD83C\uDDF5\uD83C\uDDEA" // Peru
        "PHP", "PHI" -> "\uD83C\uDDF5\uD83C\uDDED" // Philippines
        "POL" -> "\uD83C\uDDF5\uD83C\uDDF1" // Poland
        "POR" -> "\uD83C\uDDF5\uD83C\uDDF9" // Portugal

        "QAT" -> "\uD83C\uDDF6\uD83C\uDDE6" // Qatar

        "REU" -> "\uD83C\uDDF7\uD83C\uDDEA" // Réunion
        "ROM", "ROU", "ROB" -> "\uD83C\uDDF7\uD83C\uDDF4" // Romania
        "RUS" -> "\uD83C\uDDF7\uD83C\uDDFA" // Russia
        "RWA" -> "\uD83C\uDDF7\uD83C\uDDFC" // Rwanda

        "LCA" -> "\uD83C\uDDF1\uD83C\uDDE8" // Saint Lucia
        "SVG", "VIN" -> "\uD83C\uDDFB\uD83C\uDDE8" // Saint Vincent and the Grenadines
        "SAM" -> "\uD83C\uDDFC\uD83C\uDDF8" // Samoa
        "KSA" -> "\uD83C\uDDF8\uD83C\uDDE6" // Saudi Arabia
        "SCO", "SCA", "SCS" -> "\uD83C\uDFF4\uDB40\uDC67\uDB40\uDC62\uDB40\uDC73\uDB40\uDC63\uDB40\uDC74\uDB40\uDC7F" // Scotland
        "SEN" -> "\uD83C\uDDF8\uD83C\uDDF3" // Senegal
        "SRB" -> "\uD83C\uDDF7\uD83C\uDDF8" // Serbia
        "SIN", "SGP" -> "\uD83C\uDDF8\uD83C\uDDEC" // Singapore
        "SVK" -> "\uD83C\uDDF8\uD83C\uDDF0" // Slovakia
        "SVN", "SLO" -> "\uD83C\uDDF8\uD83C\uDDEE" // Slovenia
        "SOL" -> "\uD83C\uDDF8\uD83C\uDDE7" // Solomon Islands
        "RSA", "SAG", "JSB", "SAI", "SAP" -> "\uD83C\uDDFF\uD83C\uDDE6" // South Africa
        "ESP", "S23" -> "\uD83C\uDDEA\uD83C\uDDF8" // Spain
        "SRI" -> "\uD83C\uDDF1\uD83C\uDDF0" // Sri Lanka
        "SWZ" -> "\uD83C\uDDF8\uD83C\uDDFF" // Swaziland
        "SWE" -> "\uD83C\uDDF8\uD83C\uDDEA" // Sweden
        "SUI" -> "\uD83C\uDDE8\uD83C\uDDED" // Switzerland

        "PYF" -> "\uD83C\uDDF5\uD83C\uDDEB" // Tahiti
        "TAN" -> "\uD83C\uDDF9\uD83C\uDDFF" // Tanzania
        "THA" -> "\uD83C\uDDF9\uD83C\uDDED" // Thailand
        "TOG", "TG2" -> "\uD83C\uDDF9\uD83C\uDDEC" // Togo
        "TGA" -> "\uD83C\uDDF9\uD83C\uDDF4" // Tonga
        "TTO" -> "\uD83C\uDDF9\uD83C\uDDF9" // Trinidad and Tobago
        "TUN" -> "\uD83C\uDDF9\uD83C\uDDF3" // Tunisia
        "TUR" -> "\uD83C\uDDF9\uD83C\uDDF7" // Turkey
        "TCA" -> "\uD83C\uDDF9\uD83C\uDDE8" // Turks and Caicos Islands

        "UGA" -> "\uD83C\uDDFA\uD83C\uDDEC" // Uganda
        "UKR" -> "\uD83C\uDDFA\uD83C\uDDE6" // Ukraine
        "UAE" -> "\uD83C\uDDE6\uD83C\uDDEA" // United Arab Emirates
        "USA" -> "\uD83C\uDDFA\uD83C\uDDF8" // USA
        "ISV" -> "\uD83C\uDDFB\uD83C\uDDEE" // U.S. Virgin Islands
        "URU" -> "\uD83C\uDDFA\uD83C\uDDFE" // Uruguay
        "UZB" -> "\uD83C\uDDFA\uD83C\uDDFF" // Uzbekistan

        "VAN" -> "\uD83C\uDDFB\uD83C\uDDFA" // Vanuatu
        "VEN" -> "\uD83C\uDDFB\uD83C\uDDEA" // Venezuela

        "WAL", "WAA", "W23", "WAM" -> "\uD83C\uDFF4\uDB40\uDC67\uDB40\uDC62\uDB40\uDC77\uDB40\uDC6C\uDB40\uDC73\uDB40\uDC7F" // Wales
        "WXV" -> "\uD83D\uDDFA️" // World XV (using world map)

        "ZAM" -> "\uD83C\uDDFF\uD83C\uDDF2" // Zambia
        "ZIM" -> "\uD83C\uDDFF\uD83C\uDDFC" // Zimbabwe

        else -> "\uD83C\uDFC9" // Rugby ball if team can't be found / null abbreviation
    }
}
