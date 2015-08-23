package com.tomrenn.njtrains.data;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.WorkerThread;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import okio.BufferedSink;
import okio.BufferedSource;
import okio.Okio;
import okio.Sink;
import rx.Observable;
import rx.Single;
import rx.SingleSubscriber;
import rx.Subscriber;
import rx.functions.Action1;
import rx.functions.Func1;

/**
 *
 */
public class RailData {

//    private OkHttpClient httpClient;
//    private File rootDir;
//    private SQLiteDatabase db;
//
//    public RailData(OkHttpClient httpClient, File rootDir) {
//        this.httpClient = httpClient;
//        this.rootDir = rootDir;
//    }
//
//    public static Func1<File, Observable<File>> unzipRailData(final File unzipDir) {
//        return new Func1<File, Observable<File>>() {
//            @Override
//            public Observable<File> call(final File file) {
//                return Observable.create(new Observable.OnSubscribe<File>() {
//                    @Override
//                    public void call(Subscriber<? super File> subscriber) {
//                        try {
//                            if (!(unzipDir.exists() || unzipDir.mkdir())){
//                                subscriber.onError(new RuntimeException("Directory unavailable"));
//                                return;
//                            }
//                            unzip(file, unzipDir);
//                            for (File file : unzipDir.listFiles()){
//                                subscriber.onNext(file);
//                            }
//                            subscriber.onCompleted();
//                        } catch (IOException e){
//                            subscriber.onError(e);
//                        }
//                    }
//                });
//            }
//        };
//    }
//
//    /**
//     * Given a file with table_name.txt, insert rows into the associated table
//     */
//    public Action1<File> csvTableInserts = new Action1<File>() {
//        @Override
//        public void call(File file) {
//            String tableName = file.getName();
//            if (tableName.endsWith(".txt")){
//                tableName = tableName.replace(".txt", "");
//            }
//        }
//    };
//
//    public Single<File> getRailDataZip(final String url, final File file){
//        if (file.exists()){
//            return Single.just(file);
//        }
//        return Single.create(new Single.OnSubscribe<File>() {
//            @Override
//            public void call(final SingleSubscriber<? super File> singleSubscriber) {
//
//                httpClient.newCall(request)
//                        .enqueue(new Callback() {
//                            @Override
//                            public void onFailure(Request request, IOException e) {
//                                singleSubscriber.onError(e);
//                            }
//
//                            @Override
//                            public void onResponse(Response response) throws IOException {
//                                try {
//                                    saveResponseToDisk(response, file);
//                                    singleSubscriber.onSuccess(file);
//                                } catch (Exception e){
//                                    singleSubscriber.onError(e);
//                                }
//                            }
//                        });
//            }
//        });
//    }






//    STATION NAME	STATION 2CHAR
//    Aberdeen-Matawan	AM
//    Absecon	AB
//    Allendale	AZ
//    Allenhurst	AH
//    Anderson Street	AS
//    Annandale	AN
//    Asbury Park	AP
//    Atco	AO
//    Atlantic City Rail Terminal	AC
//    Avenel	AV
//    Basking Ridge	BI
//    Bay Head	BH
//    Bay Street	MC
//    Belmar	BS
//    Berkeley Heights	BY
//    Bernardsville	BV
//    Bloomfield	BM
//    Boonton	BN
//    Bound Brook	BK
//    Bradley Beach	BB
//    Brick Church	BU
//    Bridgewater	BW
//    Broadway-Fairlawn	BF
//    Campbell Hall	CB
//    Chatham	CM
//    Cherry Hill	CY
//    Clifton	IF
//    Convent Station	CN
//    Cranford	XC
//    Delawanna	DL
//    Denville	DV
//    Dover	DO
//    Dunellen	DN
//    East Orange	EO
//    Edison	ED
//    Egg Harbor City	EH
//    Elberon	EL
//    Elizabeth	EZ
//    Emerson	EN
//    Essex Street	EX
//    Fanwood	FW
//    Far Hills	FH
//    Finderne	FE
//    Garfield	GD
//    Garwood	GW
//    Gillette	GI
//    Gladstone	GL
//    Glen Ridge	GG
//    Glen Rock	RS
//    Glen Rock Boro Hall	GK
//    Great Notch	GA
//    Hackettstown	HQ
//    Hamilton	HL
//    Hammonton	HN
//    Harriman	HR
//    Hawthorne	HW
//    Hazlet	HZ
//    High Bridge	HG
//    Highland Avenue	HI
//    Hillsdale	HD
//    Hoboken	HB
//    Hohokus	UF
//    Jersey Avenue	JA
//    Kingsland	KG
//    Lake Hopatcong	HP
//    Lebanon	ON
//    Lincoln Park	LP
//    Linden	LI
//    Lindenwold	LW
//    Little Falls	FA
//    Little Silver	LS
//    Long Branch	LB
//    Lyndhurst	LN
//    Lyons	LY
//    Madison	MA
//    Mahwah	MZ
//    Manasquan	SQ
//    Maplewood	MW
//    Metro Park	MP
//    Metuchen	MU
//    Middleton NJ	MI
//    Middletown NY	MD
//    Millburn	MB
//    Millington	GO
//    Montclair State U	UV
//    Monmouth Park	MK
//    Montclair Heights	HS
//    Montvale	ZM
//    Morris Plains	MX
//    Morristown	MR
//    Mount Olive	OL
//    Mount Tabor	TB
//    Mountain Avenue	MS
//    Mountain Lakes	ML
//    Mountain Station	MT
//    Mountain View	MV
//    Murray Hill	MH
//    Nanuet	NN
//    Netcong	NT
//    Netherwood	NE
//    New Brunswick	NB
//    New Providence	NV
//    Newark Airport	NA
//    Newark Broad Street	ND
//    Newark Penn Station	NP
//    North Branch	OR
//    North Elizabeth	NZ
//    New Bridge Landing	NH
//    Oradell	OD
//    Orange	OG
//    Ottisville	OS
//    Park Ridge	PV
//    Passaic	PS
//    Paterson	RN
//    Peapack	PC
//    Pearl River	PQ
//    New york Penn Station	NY
//    Perth Amboy	PE
//    Philadelphia	PH
//    Plainfield	PF
//    Plauderville	PL
//    Point Pleasant Beach	PP
//    Port Jervis	PO
//    Princeton	PR
//    Princeton Junction	PJ
//    Radburn-Fairlawn	FZ
//    Rahway	RH
//    Ramsey	RY
//    Ramsey Rt 17	17
//    Raritan	RA
//    Red Bank	RB
//    Ridgewood	RW
//    River Edge	RG
//    Roselle Park	RL
//    Rutherford	RF
//    Salisbury Mills-Cornwall	CW
//    Secaucus Upper Lvl	SE
//    Secaucus Lower Lvl	TS
//    Short Hills	RT
//    Sloatsburg	XG
//    Somerville	SM
//    South Amboy	CH
//    South Orange	SO
//    Spring Lake	LA
//    Spring Valley	SV
//    Stirling	SG
//    Suffern	SF
//    Summit	ST
//    Teterboro	TE
//    Towaco	TO
//    Trenton	TR
//    Tuxedo	TC
//    Union	US
//    Upper Montclair	UM
//    Waldwick	WK
//    Walnut Street	WA
//    Watchung Avenue	WG
//    Watsessing Avenue	WT
//    Wayne-Route 23	23
//    Westfield	WF
//    Westwood	WW
//    White House	WH
//    Wood Ridge	WR
//    Woodbridge	WB
//    Woodcliff Lake	WL
}
