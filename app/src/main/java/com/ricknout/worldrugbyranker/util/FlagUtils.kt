package com.ricknout.worldrugbyranker.util

import android.util.Log

object FlagUtils {

    fun getFlagEmojiForTeamAbbreviation(teamAbbreviation: String): String = when (teamAbbreviation) {
        "NZL" -> "\uD83C\uDDF3\uD83C\uDDFF" // New Zealand
        "IRE" -> "\uD83C\uDDEE\uD83C\uDDEA" // Ireland
        "WAL" -> "\uD83C\uDFF4\uDB40\uDC67\uDB40\uDC62\uDB40\uDC77\uDB40\uDC6C\uDB40\uDC73\uDB40\uDC7F" // Wales
        "ENG" -> "\uD83C\uDFF4\uDB40\uDC67\uDB40\uDC62\uDB40\uDC65\uDB40\uDC6E\uDB40\uDC67\uDB40\uDC7F" // England
        "RSA" -> "\uD83C\uDDFF\uD83C\uDDE6" // South Africa
        "SCO" -> "\uD83C\uDFF4\uDB40\uDC67\uDB40\uDC62\uDB40\uDC73\uDB40\uDC63\uDB40\uDC74\uDB40\uDC7F" // Scotland
        "AUS" -> "\uD83C\uDDE6\uD83C\uDDFA" // Australia
        "FRA" -> "\uD83C\uDDEB\uD83C\uDDF7" // France
        "ARG" -> "\uD83C\uDDE6\uD83C\uDDF7" // Argentina
        "FJI" -> "\uD83C\uDDEB\uD83C\uDDEF" // Fiji
        "JPN" -> "\uD83C\uDDEF\uD83C\uDDF5" // Japan
        "TGA" -> "\uD83C\uDDF9\uD83C\uDDF4" // Tonga
        "GEO" -> "\uD83C\uDDEC\uD83C\uDDEA" // Georgia
        "ITA" -> "\uD83C\uDDEE\uD83C\uDDF9" // Italy
        "USA" -> "\uD83C\uDDFA\uD83C\uDDF8" // USA
        "SAM" -> "\uD83C\uDDFC\uD83C\uDDF8" // Samoa
        "ROM" -> "\uD83C\uDDF7\uD83C\uDDF4" // Romania
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
        "CHL" -> "\uD83C\uDDE8\uD83C\uDDF1" // Chile
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
        "PHP" -> "\uD83C\uDDF5\uD83C\uDDED" // Philippines
        "COK" -> "\uD83C\uDDE8\uD83C\uDDF0" // Cook Islands
        "SEN" -> "\uD83C\uDDF8\uD83C\uDDF3" // Senegal
        "CRO" -> "\uD83C\uDDED\uD83C\uDDF7" // Croatia
        "SWE" -> "\uD83C\uDDF8\uD83C\uDDEA" // Sweden
        "GUY" -> "\uD83C\uDDEC\uD83C\uDDFE" // Guyana
        "ISR" -> "\uD83C\uDDEE\uD83C\uDDF1" // Israel
        "LAT" -> "\uD83C\uDDF1\uD83C\uDDFB" // Latvia
        "SIN" -> "\uD83C\uDDF8\uD83C\uDDEC" // Singapore
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
        "NGA" -> "\uD83C\uDDF3\uD83C\uDDEC" // Nigeria
        "BWA" -> "\uD83C\uDDE7\uD83C\uDDFC" // Botswana
        "AUT" -> "\uD83C\uDDE6\uD83C\uDDF9" // Austria
        "BER" -> "\uD83C\uDDE7\uD83C\uDDF2" // Bermuda
        "SVN" -> "\uD83C\uDDF8\uD83C\uDDEE" // Slovenia
        "THA" -> "\uD83C\uDDF9\uD83C\uDDED" // Thailand
        "DEN" -> "\uD83C\uDDE9\uD83C\uDDF0" // Denmark
        "PER" -> "\uD83C\uDDF5\uD83C\uDDEA" // Peru
        "SVG" -> "\uD83C\uDDFB\uD83C\uDDE8" // St Vincent and Grenadines
        "SRB" -> "\uD83C\uDDF7\uD83C\uDDF8" // Serbia
        "BRB" -> "\uD83C\uDDE7\uD83C\uDDE7" // Barbados
        "IND" -> "\uD83C\uDDEE\uD83C\uDDF3" // India
        "PYF" -> "\uD83C\uDDF5\uD83C\uDDEB" // Tahiti
        "BHS" -> "\uD83C\uDDE7\uD83C\uDDF8" // Bahamas
        "CHN" -> "\uD83C\uDDE8\uD83C\uDDF3" // China
        "SWZ" -> "\uD83C\uDDF8\uD83C\uDDFF" // Swaziland
        "GHA" -> "\uD83C\uDDEC\uD83C\uDDED" // Ghana
        "UZB" -> "\uD83C\uDDFA\uD83C\uDDFF" // Uzbekistan
        "PAK" -> "\uD83C\uDDF5\uD83C\uDDF0" // Pakistan
        "MUS" -> "\uD83C\uDDF2\uD83C\uDDFA" // Mauritius
        "CTR" -> "\uD83C\uDDE8\uD83C\uDDF7" // Costa Rica
        "BUL" -> "\uD83C\uDDE7\uD83C\uDDEC" // Bulgaria
        "RWA" -> "\uD83C\uDDF7\uD83C\uDDFC" // Rwanda
        "NOR" -> "\uD83C\uDDF3\uD83C\uDDF4" // Norway
        "IDO" -> "\uD83C\uDDEE\uD83C\uDDE9" // Indonesia
        "NIU" -> "\uD83C\uDDF3\uD83C\uDDFA" // Niue Island
        "FIN" -> "\uD83C\uDDEB\uD83C\uDDEE" // Finland
        "CAM" -> "\uD83C\uDDE8\uD83C\uDDF2" // Cameroon
        "SOL" -> "\uD83C\uDDF8\uD83C\uDDE7" // Solomon Islands
        "MON" -> "\uD83C\uDDF2\uD83C\uDDE8" // Monaco
        "HEL" -> "\uD83C\uDDEC\uD83C\uDDF7" // Greece
        "VAN" -> "\uD83C\uDDFB\uD83C\uDDFA" // Vanuatu
        "ASM" -> "\uD83C\uDDE6\uD83C\uDDF8" // American Samoa
        else -> {
            Log.e(TAG, "Could not find flag emoji for $teamAbbreviation")
            "\uD83C\uDFF3️" // White flag if team can't be found
        }
    }

    private const val TAG = "FlagUtils"
}