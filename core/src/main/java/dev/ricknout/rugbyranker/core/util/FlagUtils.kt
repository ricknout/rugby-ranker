package dev.ricknout.rugbyranker.core.util

object FlagUtils {

    fun getFlagEmojiForTeamAbbreviation(teamAbbreviation: String?) = when (teamAbbreviation) {
        "NZL" -> "\uD83C\uDDF3\uD83C\uDDFF" // New Zealand
        "IRE" -> "\u2618\uFE0F" // Ireland (using a shamrock for IRFU)
        "WAL" -> "\uD83C\uDFF4\uDB40\uDC67\uDB40\uDC62\uDB40\uDC77\uDB40\uDC6C\uDB40\uDC73\uDB40\uDC7F" // Wales
        "ENG" -> "\uD83C\uDFF4\uDB40\uDC67\uDB40\uDC62\uDB40\uDC65\uDB40\uDC6E\uDB40\uDC67\uDB40\uDC7F" // England
        "RSA" -> "\uD83C\uDDFF\uD83C\uDDE6" // South Africa
        "SCO" -> "\uD83C\uDFF4\uDB40\uDC67\uDB40\uDC62\uDB40\uDC73\uDB40\uDC63\uDB40\uDC74\uDB40\uDC7F" // Scotland
        "AUS" -> "\uD83C\uDDE6\uD83C\uDDFA" // Australia
        "FRA" -> "\uD83C\uDDEB\uD83C\uDDF7" // France
        "ARG" -> "\uD83C\uDDE6\uD83C\uDDF7" // Argentina
        "FJI", "FIJ" -> "\uD83C\uDDEB\uD83C\uDDEF" // Fiji
        "JPN" -> "\uD83C\uDDEF\uD83C\uDDF5" // Japan
        "TGA" -> "\uD83C\uDDF9\uD83C\uDDF4" // Tonga
        "GEO" -> "\uD83C\uDDEC\uD83C\uDDEA" // Georgia
        "ITA" -> "\uD83C\uDDEE\uD83C\uDDF9" // Italy
        "USA" -> "\uD83C\uDDFA\uD83C\uDDF8" // USA
        "SAM" -> "\uD83C\uDDFC\uD83C\uDDF8" // Samoa
        "ROM", "ROU" -> "\uD83C\uDDF7\uD83C\uDDF4" // Romania
        "URU" -> "\uD83C\uDDFA\uD83C\uDDFE" // Uruguay
        "RUS" -> "\uD83C\uDDF7\uD83C\uDDFA" // Russia
        "ESP" -> "\uD83C\uDDEA\uD83C\uDDF8" // Spain
        "HKG" -> "\uD83C\uDDED\uD83C\uDDF0" // Hong Kong
        "NAM" -> "\uD83C\uDDF3\uD83C\uDDE6" // Namibia
        "CAN" -> "\uD83C\uDDE8\uD83C\uDDE6" // Canada
        "POR" -> "\uD83C\uDDF5\uD83C\uDDF9" // Portugal
        "BEL" -> "\uD83C\uDDE7\uD83C\uDDEA" // Belgium
        "BRA" -> "\uD83C\uDDE7\uD83C\uDDF7" // Brazil
        "NED" -> "\uD83C\uDDF3\uD83C\uDDF1" // Netherlands
        "KEN" -> "\uD83C\uDDF0\uD83C\uDDEA" // Kenya
        "GER" -> "\uD83C\uDDE9\uD83C\uDDEA" // Germany
        "CHL", "CHI" -> "\uD83C\uDDE8\uD83C\uDDF1" // Chile
        "KOR" -> "\uD83C\uDDF0\uD83C\uDDF7" // Korea
        "CZE" -> "\uD83C\uDDE8\uD83C\uDDFF" // Czechia
        "SUI" -> "\uD83C\uDDE8\uD83C\uDDED" // Switzerland
        "LTU" -> "\uD83C\uDDF1\uD83C\uDDF9" // Lithuania
        "POL" -> "\uD83C\uDDF5\uD83C\uDDF1" // Poland
        "COL" -> "\uD83C\uDDE8\uD83C\uDDF4" // Colombia
        "UKR" -> "\uD83C\uDDFA\uD83C\uDDE6" // Ukraine
        "PAR" -> "\uD83C\uDDF5\uD83C\uDDFE" // Paraguay
        "ZIM" -> "\uD83C\uDDFF\uD83C\uDDFC" // Zimbabwe
        "MLT" -> "\uD83C\uDDF2\uD83C\uDDF9" // Malta
        "TUN" -> "\uD83C\uDDF9\uD83C\uDDF3" // Tunisia
        "UGA" -> "\uD83C\uDDFA\uD83C\uDDEC" // Uganda
        "SRI" -> "\uD83C\uDDF1\uD83C\uDDF0" // Sri Lanka
        "MDA" -> "\uD83C\uDDF2\uD83C\uDDE9" // Moldova
        "CIV" -> "\uD83C\uDDE8\uD83C\uDDEE" // Cote D'Ivoire
        "MAR" -> "\uD83C\uDDF2\uD83C\uDDE6" // Morocco
        "MAS" -> "\uD83C\uDDF2\uD83C\uDDFE" // Malaysia
        "MEX" -> "\uD83C\uDDF2\uD83C\uDDFD" // Mexico
        "TTO" -> "\uD83C\uDDF9\uD83C\uDDF9" // Trinidad & Tobago
        "MAD" -> "\uD83C\uDDF2\uD83C\uDDEC" // Madagascar
        "CAY" -> "\uD83C\uDDF0\uD83C\uDDFE" // Cayman Islands
        "PHP", "PHI" -> "\uD83C\uDDF5\uD83C\uDDED" // Philippines
        "COK" -> "\uD83C\uDDE8\uD83C\uDDF0" // Cook Islands
        "SEN" -> "\uD83C\uDDF8\uD83C\uDDF3" // Senegal
        "CRO" -> "\uD83C\uDDED\uD83C\uDDF7" // Croatia
        "SWE" -> "\uD83C\uDDF8\uD83C\uDDEA" // Sweden
        "GUY" -> "\uD83C\uDDEC\uD83C\uDDFE" // Guyana
        "ISR" -> "\uD83C\uDDEE\uD83C\uDDF1" // Israel
        "LAT" -> "\uD83C\uDDF1\uD83C\uDDFB" // Latvia
        "SIN", "SGP" -> "\uD83C\uDDF8\uD83C\uDDEC" // Singapore
        "KAZ" -> "\uD83C\uDDF0\uD83C\uDDFF" // Kazakhstan
        "VEN" -> "\uD83C\uDDFB\uD83C\uDDEA" // Venezuela
        "TPE" -> "\uD83C\uDDF9\uD83C\uDDFC" // Chinese Taipei
        "LUX" -> "\uD83C\uDDF1\uD83C\uDDFA" // Luxembourg
        "JAM" -> "\uD83C\uDDEF\uD83C\uDDF2" // Jamaica
        "ZAM" -> "\uD83C\uDDFF\uD83C\uDDF2" // Zambia
        "PNG" -> "\uD83C\uDDF5\uD83C\uDDEC" // Papua New Guinea
        "HUN" -> "\uD83C\uDDED\uD83C\uDDFA" // Hungary
        "GUM" -> "\uD83C\uDDEC\uD83C\uDDFA" // Guam
        "AND" -> "\uD83C\uDDE6\uD83C\uDDE9" // Andorra
        "BIH" -> "\uD83C\uDDE7\uD83C\uDDE6" // Bosnia & Herzegovina
        "UAE" -> "\uD83C\uDDE6\uD83C\uDDEA" // United Arab Emirates
        "NGA", "NGR" -> "\uD83C\uDDF3\uD83C\uDDEC" // Nigeria
        "BWA", "BOT" -> "\uD83C\uDDE7\uD83C\uDDFC" // Botswana
        "AUT" -> "\uD83C\uDDE6\uD83C\uDDF9" // Austria
        "BER" -> "\uD83C\uDDE7\uD83C\uDDF2" // Bermuda
        "SVN", "SLO" -> "\uD83C\uDDF8\uD83C\uDDEE" // Slovenia
        "THA" -> "\uD83C\uDDF9\uD83C\uDDED" // Thailand
        "DEN" -> "\uD83C\uDDE9\uD83C\uDDF0" // Denmark
        "PER" -> "\uD83C\uDDF5\uD83C\uDDEA" // Peru
        "SVG", "VIN" -> "\uD83C\uDDFB\uD83C\uDDE8" // St Vincent and Grenadines
        "SRB" -> "\uD83C\uDDF7\uD83C\uDDF8" // Serbia
        "BRB", "BAR" -> "\uD83C\uDDE7\uD83C\uDDE7" // Barbados
        "IND" -> "\uD83C\uDDEE\uD83C\uDDF3" // India
        "PYF" -> "\uD83C\uDDF5\uD83C\uDDEB" // Tahiti
        "BHS", "BAH" -> "\uD83C\uDDE7\uD83C\uDDF8" // Bahamas
        "CHN" -> "\uD83C\uDDE8\uD83C\uDDF3" // China
        "SWZ" -> "\uD83C\uDDF8\uD83C\uDDFF" // Swaziland
        "GHA" -> "\uD83C\uDDEC\uD83C\uDDED" // Ghana
        "UZB" -> "\uD83C\uDDFA\uD83C\uDDFF" // Uzbekistan
        "PAK" -> "\uD83C\uDDF5\uD83C\uDDF0" // Pakistan
        "MUS", "MRI" -> "\uD83C\uDDF2\uD83C\uDDFA" // Mauritius
        "CTR", "CRC" -> "\uD83C\uDDE8\uD83C\uDDF7" // Costa Rica
        "BUL" -> "\uD83C\uDDE7\uD83C\uDDEC" // Bulgaria
        "RWA" -> "\uD83C\uDDF7\uD83C\uDDFC" // Rwanda
        "NOR" -> "\uD83C\uDDF3\uD83C\uDDF4" // Norway
        "IDO", "INA" -> "\uD83C\uDDEE\uD83C\uDDE9" // Indonesia
        "NIU" -> "\uD83C\uDDF3\uD83C\uDDFA" // Niue Island
        "FIN" -> "\uD83C\uDDEB\uD83C\uDDEE" // Finland
        "CAM", "CMR" -> "\uD83C\uDDE8\uD83C\uDDF2" // Cameroon
        "SOL" -> "\uD83C\uDDF8\uD83C\uDDE7" // Solomon Islands
        "MON" -> "\uD83C\uDDF2\uD83C\uDDE8" // Monaco
        "HEL", "GRE" -> "\uD83C\uDDEC\uD83C\uDDF7" // Greece
        "VAN" -> "\uD83C\uDDFB\uD83C\uDDFA" // Vanuatu
        "ASM", "ASA" -> "\uD83C\uDDE6\uD83C\uDDF8" // American Samoa
        "ALG" -> "\uD83C\uDDE9\uD83C\uDDFF" // Algeria
        "MNE" -> "\uD83C\uDDF2\uD83C\uDDEA" // Montenegro
        "EST" -> "\uD83C\uDDEA\uD83C\uDDEA" // Estonia
        "TUR" -> "\uD83C\uDDF9\uD83C\uDDF7" // Turkey
        "CYP" -> "\uD83C\uDDE8\uD83C\uDDFE" // Cyprus
        "SVK" -> "\uD83C\uDDF8\uD83C\uDDF0" // Slovakia
        "LCA" -> "\uD83C\uDDF1\uD83C\uDDE8" // St Lucia
        "GLP" -> "\uD83C\uDDEC\uD83C\uDDF5" // Guadeloupe
        "IVB" -> "\uD83C\uDDFB\uD83C\uDDEC" // British Virgin Islands
        "NRU" -> "\uD83C\uDDF3\uD83C\uDDF7" // Nauru
        "TCA" -> "\uD83C\uDDF9\uD83C\uDDE8" // Turks and Caicos Islands
        "CUW" -> "\uD83C\uDDE8\uD83C\uDDFC" // Curacao
        "QAT" -> "\uD83C\uDDF6\uD83C\uDDE6" // Qatar
        "LIB" -> "\uD83C\uDDF1\uD83C\uDDE7" // Lebanon
        "JOR" -> "\uD83C\uDDEF\uD83C\uDDF4" // Jordan
        "MTO" -> "\uD83C\uDDF2\uD83C\uDDF6" // Martinique
        "GUA" -> "\uD83C\uDDEC\uD83C\uDDF9" // Guatemala
        "PAN" -> "\uD83C\uDDF5\uD83C\uDDE6" // Panama
        "ESA" -> "\uD83C\uDDF8\uD83C\uDDFB" // El Salvador
        "NIG" -> "\uD83C\uDDF3\uD83C\uDDEA" // Niger
        "MGL" -> "\uD83C\uDDF2\uD83C\uDDF3" // Mongolia
        "KGZ" -> "\uD83C\uDDF0\uD83C\uDDEC" // Kyrgyzstan
        "BRU" -> "\uD83C\uDDE7\uD83C\uDDF3" // Brunei
        "LES" -> "\uD83C\uDDF1\uD83C\uDDF8" // Lesotho
        "IRI" -> "\uD83C\uDDEE\uD83C\uDDF7" // Iran
        "LAO" -> "\uD83C\uDDF1\uD83C\uDDE6" // Laos
        "MAW" -> "\uD83C\uDDF2\uD83C\uDDFC" // Malawi
        "TOG" -> "\uD83C\uDDF9\uD83C\uDDEC" // Togo
        "BEN" -> "\uD83C\uDDE7\uD83C\uDDEF" // Benin
        "ECU" -> "\uD83C\uDDEA\uD83C\uDDE8" // Ecuador
        "COD" -> "\uD83C\uDDE8\uD83C\uDDE9" // Democratic Republic of Congo
        "BDI" -> "\uD83C\uDDE7\uD83C\uDDEE" // Burundi
        "BLR" -> "\uD83C\uDDE7\uD83C\uDDFE" // Belarus
        "KSA" -> "\uD83C\uDDF8\uD83C\uDDE6" // Saudi Arabia
        "BUR" -> "\uD83C\uDDE7\uD83C\uDDEB" // Burkina Faso
        "MLI" -> "\uD83C\uDDF2\uD83C\uDDF1" // Mali
        "BIL" -> "\uD83E\uDD81" // British & Irish Lions (using a lion)
        "WXV" -> "\uD83D\uDDFA️" // World XV (using world map)
        else -> "\uD83C\uDFC9" // Rugby ball if team can't be found
        // Teams with null abbreviations: Dominican Republic, Honduras, Nicaragua
    }
}
