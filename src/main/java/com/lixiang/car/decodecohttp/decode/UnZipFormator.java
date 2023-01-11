package com.lixiang.car.decodecohttp.decode;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.lixiang.car.decodecohttp.decodecohttp.util.StringsKt;
import com.lixiang.car.decodecohttp.util.FileUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;


public class UnZipFormator {

    public static String format(String content) {
        System.out.println("format text ->>>" + content);
        String jsonStr = uZip(Base64.getDecoder().decode(replaceBlank(content)));
        JsonObject jsonObject = JsonParser.parseString(jsonStr).getAsJsonObject();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(jsonObject);
    }

    public static String uZip(byte[] compressed) {
        if (compressed == null) {
            return "";
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ByteArrayInputStream in = null;
        GZIPInputStream ginzip = null;
        String decompressed = null;
        try {
            in = new ByteArrayInputStream(compressed);
            ginzip = new GZIPInputStream(in);

            byte[] buffer = new byte[1024];
            int offset = -1;
            while ((offset = ginzip.read(buffer)) != -1) {
                out.write(buffer, 0, offset);
            }
            decompressed = out.toString();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (ginzip != null) {
                try {
                    ginzip.close();
                } catch (IOException ignored) {
                }
            }
            if (in != null) {
                try {
                    in.close();
                } catch (IOException ignored) {
                }
            }
            try {
                out.close();
            } catch (IOException ignored) {
            }
        }
        return decompressed;
    }

    public static String replaceBlank(String str) {
        String dest = "";
        if (str != null) {
            Pattern p = Pattern.compile("\\s*|\t|\r|\n");
            Matcher m = p.matcher(str);
            dest = m.replaceAll("");
        }
        return dest;
    }

    public static void main(String[] args) {
        String s = "2023-01-12 10:37:29.418 12836-16908/com.lixiang.car.huanxi I/CoHttpHttpLogger: ┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄以下内容为返回结果┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄\n" +
                "    ***H4sIAAAAAAAAAO1cW1Mc17X+K6fmmbL3Ze2bHpOKqvJy6lQd5+nUeejdF4kEC4pLTlwuV4FlzE1YlyDASLKuRMZCIIwugLCoyk85me6eefJfyNrgiIHuHvdOYgNR0BQFGqa7Z7691vq+tb7dHzeGRsIwHhpqnBseHIl7GmF/FDfOkZ5GFAwHjXMfN4b7h4O+X/aPXBpunAPT0xiMh0b6hvHv/+fjxu97o7j/P0c+bJxjPQe/fNA73Ievb6SPNlrXvsuXPks/v5XeetDoaQz0BR/912D/hcH9kzHCVU+jd+h876XeoYtxtH/Ki71Dw/2DH/1mAM8df9D7IR6ISsXBECqEu7S+vjgc7u2/9OvIPUOUlKCYJFIrxpUmnX/ywUcD+HLxw2UdfYECzYggCt/N8A/Xm489bd9/2dyeycfeuIvtHxqOB38z2IdPXRweHhg69/77A72hee/iSHDpD73vhf0fvq8DE1tCEsUFY4oGhCquQISWcysT***\n" +
                "    ***Y9777cAFPFQ0Mhi4C8L3LDl+TAMjtg/f8sHbuzTS19fTsEF0IXaf9e+DvpH9q5lZzW48bbhnwt9dGMQPP/rv4Y/2L5T84fz5XzBGGMGnk/5Lw4dP/Eqc3/9qfNLTGLrY/3/DvQNDnUeV7Vufp+NfVxxWk4OvwmHPnz887GDcFwdD8a8vJf0HF/9JT+cqOHg7nQvh4H+OYk9qA88Y1xXAc8KMUVQrZSQrAk87gD+4hr8h3dyaTa9NZgsv07XbuD6bOzvVeItOvPv7tGCUkUAQYTVQLSxXJiFJYKMYNC1BXGsufn7ED05xAlCRUqiE4cSAFhojVQkfqNKrs+nsq3T5ZT65VQ2SPgoS40mEZyTEiARUpAIIldEBTSKuEhHRAkiCG3h3QFJQHk8MjNb4BghoXpJIq0FqLe9l63PpzI2aYfQ2bWptFcUzUqUN5SYAsCShxSASuILeHXykLMWHc8KF0kwILqlXvkun7+Ej+2qhGh91NIJiJkJJ***\n" +
                "    ***wzimsQBFiLaCEBkIyYy2JgmLCAl4lxAqT3NYkbQhTCG7oH4Itee+bO4upbPT+dxdX+5hDUQJSAwiaySlBqylPJJFiLSg7w5EgpdDhHQBLx40EkAufSDKnz9or6y2J65kL17lt7qEEi1FibIgCoMEUaJKhQGePkGyWEx1uIjeIZRAVQUSB9DIFUBRL5TStaV08mU+9yq9OlOf1FHOIkMpfsR42oRry2QQEAnWRDzirKwe6XcIpCpSZ7QEAgK0dDqqPkjZneVsYTmb327f3KtZkt7GETI7cxBHSRSTUCBQCjgvQkT0CSitfyJEqG2YD0q8KuFRRYQhSgtUyV416duxfGcTHyiJs6dXfLldlFipQtTtKo4iTTDhAkt4UEx4KJ3PNFBescSrmAMVShLCJRPMC6Xs6vN0eqUaHDiW6lioICRIEVgIsVY6CWUUGGMDHYDUcTGOcPW8M/Agu60kdkwqJfAdMO4Fz+IX6aOVbPJ6trjeejZWjRM/***\n" +
                "    ***ilMUYV7jJNDEBpCAsMagnKU2FCFhYUCKOFF9tksSGGK8oKIVVUkyKgVF7S+pl4rNZ3bTpS982XcANEZN5Ng3FQG3BLkBkUXKIDV7dwQSUyW8DiWR4lxJYJwB10x78brr37VH5/KZT9PluWqI5NEgstLipYjIIF0DiFwMUxohsUs4M0jxCiCBoWe7FnEfkCr6DC6CGGhjsDBLL17XWl/o2kstZ3QRiQ1GDEZQZDS1hgWcxxoK4CjupMAZBod6gVNBFJhiWnIsSAKoVyVK119nG1Ottfu+Gc6QkBrlZhsGMMeFHAOIxOq9gUvHMhyTZ5speOHTpb+A9UdpqpjyU67XttM/ft01flgZPjIJtBRWEyoTy2KrdUBJTEomEYzwM42PJOCV36CcIWANEhq/DFIm8BpGtN98mS5vpk+70G12tAJRYBCaSIRxqIEgYEAFhBGV2iJXsMUmnWRuKFoLpMtPTyNIiigvpsDLixBi7Ri3VgZTofJi3Bt3***\n" +
                "    ***s+359LM9hMqzFEkpQsoQKOp+SuI4RuFaOtQj6mzPi7DI+2Q7yiqqEVWgFa5mqaRnNZpdyOa3W9/9KX/yZb70WWtsqbm7VA0XKa1MRFgZ71cmImUiAEUs6Kikp7pvDDjLcPlgRauwMsZ1U5HXSeJF6/L5dSTezsjxaD5bvNu1RJVSCGkohIwLjCsj8FcFseVARIlIMme7RIHm9UUskYJV9L+pEgwUGIHCxWuQ1Fpfz+6M1lRIh4EUxCyE0AVSqGUQCYrVs6w6KXFaKR49Ck6jubueTq6k49tF3xEID4yY1BU9ISAciZfQQkpVglGJ74hwozmAkNRRk7esYmyvuTXfmr2Rboz/AwN0HTKiieSUlAwsmNJdA+vglzLHEPzkjqHjvrHW3q104jWmm2zzJpaJ1vp0NjVa4h5jtXkGZh5xdEJxCIognDOqJEEYaR0U8QXON4Y0XwB0eoom8qmVfO5uPvbiP/LV1ebOlfTpw9qtClS8QWAg4IkW***\n" +
                "    ***oEJiQi1sbMOE4T9T0u9D9X4C/aTDo6pT4iRDongsmmnXdcAr1gEyGYxjpOuuf+dVHZe/yh8v7tvIvsaM8+dX+dJaOt5l+KjLVVwoIRaRS8FERzIKbExpWOyCHLzfU5mC+XED6MROPrOR3riSji/jD8UQNro2seHAtaoygArm3A+MIFevl4iFy+oGtZaETgPop7s/XKZPe6RjzkViw3EJxVhPi71FTk+ifXV4VP6TByw9nsmzy5vp3Eb6bAO/f797xf38ZiH942xzZ62wGKTUHmtBVg1qgADgHxj8DrXSOdJQjkKCuO607CjKD7/Ip1fTZ9fT7ef7zHclX6nrEn27NMAKlQSuOoONE1DcRBSTeXFpyJOYDRwelZ4WW7CgBGqvAmoq7SMMqHATb6o9OzzNrdH88Y32jS4mrHIpKjGjWMoRamm4ZTFhCQ+TqKy/c8YVDtUeEIFQFYGKcpAhV1XCgPFSo+mjnfTzezUH3m/xEZIHKnRlQkQm***\n" +
                "    ***iY1VmgZQxEcTfVoVTk3bCC5+H3xEBR9igCsV2RBDvuFnSNhayZbmHX2f3sCHr09OSYtMzPl7lErC0AZaUhboEil6aqdBx6VovrGZfj3+51fZi1fprXvZ/Hqx+KF0qwsakyBNlUNYYt4zQqLcK5vcFaqfewGmLm0Mvo50MKHJjezyOIqZ7PnrOmKGHffTBSEIYiEiDJSKA8OjmGO4iYTrkBblKfK2EzAvdBRA8tOTo+OLYnWV4kkLS4FzU5sUOys4reifgxEYwtJQZcrmUCVEyAiOyQ+c/1x1NJSeP2/tPGnu3WnfnK/pXnmraSwkceiosbTWUoukLaaW2yL/AXkCntczvCsKgWeyYgBJUAhxI6ik0jNxTy7mY0/ya5ezxS7050fUq0wSS1HABlLYkkBHll93vPVzZ+3j4gUDlJUFKGW1bZkOJqjYbAOSUWD73SdWT6igSAAileHazXEPUbvusvW3m82t2/nMmm/n962dVhMIIqIgCUQg***\n" +
                "2023-01-12 10:37:29.418 12836-16908/com.lixiang.car.huanxi I/CoHttpHttpLogger: ***VLHdpLtn6OoGIv/5c2u6e681tdN6NvP/o3P4aO19mS2uI7Vvbn2DH1e6d3n/8QIfzZ3P0zeTze1xx1dujjd3Zgp4g/GozYIaqOpSUGZQaTKp9psONQDHF0hQRHMBHcr0L6PTb1XKX0Zn8p1v0u9u5lMTvn2LTkYMGLFChMwGJa1jWnsifUb7FrX9iT7rgFf4EyWlmBUoYeqgJ++1FbJ950G2cA+pWW0qpojiiZU6siFAwLhORBKAa1WFkYqi4kYuLBtn2wKHKbW2AEKciKjcDUlRH6FA1YJ74dR+vYjh2Hr8sLX+PL2+6EmaOjoJirnTE2cMj5OiHY7Rs91J8PEgOKB4OVBub7HmTFMtDHg5RZpb8/nNK+n4avbsum/dtAFJKHcTU5vQIMIkShmnxdaeAH1a2/U1w8ljGwrSGcOqTKVKCTAobpC+gF+/52p2+0m696S5fc3tRFnokvyOmegtSUQkQuRLKD6Z5shII4QpRnFsXPup6P/V***\n" +
                "    ***7LT6egoc59FE+6vZ5tZ0e2U1vfb4+90rrTe3vt8dK+Or9QUlGHV0hNK5Q0UB8k/DQfASAIv8xb1Aa8OUEaJjUNqa/Cq9+iydelynsUCPAgqYi+OERyYEC7EOrYwtig8WsYQRaoq0lTJ1op11fUrIi9xvstZdBEhRK7uChjMu3D03eIlq6SIup75IZ++3J640d5b2e4NT2UwXG2V5aTzsDWoAgyuAAEtU8XYOisvTqjJr2vgl94hZqapsREgxlXb2PUG0V2lU+n3BfI3ISuBX4to9SikdiJhbESfFoBT79pczDI/Q0gceSSodedrdOIgbpJpeNTG7M5pNzaAMTK++8hxKSg1ESI1BJLUg0rVtlDOilW1YOq1BdLwWYgnMZqayO2+Kxa8uTMT5f6qUO5IaAxJlAJASJVDWSmUauSgjcMTola4soXRL9y639rb3ix/i1634VWyXOfQbKMmdCnBd4JIByUnc0uZkW+o7m/m32+2Fb9KJ19nm***\n" +
                "    ***g3T8mTMdbG+mE/fxc2++eXjQ4Ww/XGzfellcKsJrrVTt43XIS0y3wohaXR7ODHcGaqSjihxvxmYv1rreEKR0hCYja3TCLUY4/hRRayTqFFZ0c2LuOdG2+09vISuYQnd20r3x9v3X2e2p7MGfssubhWVQuwfvNmtD1fCFIVfWoLTTprUyBlNCYaEmYIjsYir8kYxRPvvuGKmahOgEEz5hoqh+mNYnmjJ+/vVw8JG21t98v7uUzdzIX99Jdz9Nbz/Lxop9YK+Fwav2kVPUV4QSpmm9qRx3jXmCK4nSzoVRGND+XaUktAkmCOJ2XhKeCHf/RGV00WtKFf1XH88Wb175uPXiQbr2Mpucb42O46/tbxbSp9cKy6L2LSxxWUCla41yw4ymiPLRwlI9G+BGA3eCXHQMa9sLa/nTLvujy/NDh3cJbEhiGgUqliX5QYl/TwI8Nlkzo1ilNMPCoChmYoapwMtv3HztbhKW3R5Nl93NVbPbM11vdVQe***\n" +
                "    ***+R13LeCxTmyoDA102d2o1CnaOfW/+FcfDl3Yp0fX0um7jU/+ClnyKvy8VgAA*** \n" +
                "2023-01-12 10:37:29.418 12836-16908/com.lixiang.car.huanxi I/CoHttpHttpLogger: ────────────────────────────────────────────────────────";
        extracted(s);

    }

    public static String extracted(String s) {
        List<String> strs = new ArrayList<>();
        Pattern p = Pattern.compile("(\\*\\*\\*)(([\\s\\S])*?)(\\*\\*\\*)");
        Matcher m = p.matcher(s);
        while(m.find()) {
            String group = m.group();
            strs.add(group);
        }
        StringBuilder builder = new StringBuilder();
        for (String str : strs) {
            if (str.contains("Cohttp")){
                continue;
            }
            builder.append(StringsKt.legal(str,true));
        }
        return builder.toString();
    }
}
