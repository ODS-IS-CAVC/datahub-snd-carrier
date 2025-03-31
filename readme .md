# datahub-snd-carrier

## リポジトリの概要
共同輸送システムにデータを送受信するためのAPI
	
## 機能説明	
登録（更新）：HTTPレスポンスにてDBから抽出したZip圧縮されたCSVデータを、JSONデータに変換し、HTTPリクエストにて共同輸送システムに送信する
削除：削除したいレコードの主キーとなる値をクエリパラメータにしHTTPリクエストにて共同輸送システムに送信する
参照：HTTPレスポンスにて共同輸送システムから受信したJSONデータをCSVデータに変換、Zip圧縮し、HTTPリクエストにてDBに格納する

## 使用言語
JavaSE-17

## 環境
Gradle 8.10.2
Eclipse 2023-12 (4.30.0)

## システム構成図、開発環境の構築方法

`datahub-snd-carrier`
`　─src`
`    └─main`
`        ├─java`
`        │  └─datahub`
`        │      │  DatahubJsontocsvApplication.java`
`        │      │`
`        │      ├─common`
`        │      │      CarrierApiEndpoint.java`
`        │      │      Constants.java`
`        │      │      DataTypeModelMapper.java`
`        │      │`
`        │      ├─config`
`        │      │      ApiConfig.java`
`        │      │`
`        │      ├─exception`
`        │      │      DataHubException.java`
`        │      │`
`        │      ├─job`
`        │      │      Job.java`
`        │      │`
`        │      ├─model`
`        │      │      CarInfo.java`
`        │      │      CarrierApiRequest.java`
`        │      │      CarrierTrans.java`
`        │      │      CarrierTransCapacitySearchRequest.java`
`        │      │      CarrierTransRequestSearchRequest.java`
`        │      │      CneePrty.java`
`        │      │      Cns.java`
`        │      │      CnsgPrty.java`
`        │      │      CnsLineItem.java`
`        │      │      CutOffInfo.java`
`        │      │      DelInfo.java`
`        │      │      DrvAvbTime.java`
`        │      │      DrvInfo.java`
`        │      │      FreeTimeInfo.java`
`        │      │      FretClimToPrty.java`
`        │      │      HazardousMaterialInfo.java`
`        │      │      HealthCheckRequest.java`
`        │      │      InputParameters.java`
`        │      │      LogsSrvcPrv.java`
`        │      │      MaxCarryingCapacity.java`
`        │      │      MotasInfo.java`
`        │      │      MsgInfo.java`
`        │      │      RoadCarr.java`
`        │      │      ShipFromPrty.java`
`        │      │      ShipFromPrtyRqrm.java`
`        │      │      ShipperTransCapacity.java`
`        │      │      ShipperTransCapacitySearchRequest.java`
`        │      │      ShipToPrty.java`
`        │      │      ShipToPrtyRqrm.java`
`        │      │      TrspAbilityLineItem.java`
`        │      │      TrspIsr.java`
`        │      │      TrspPlan.java`
`        │      │      TrspPlanLineItem.java`
`        │      │      TrspRqrPrty.java`
`        │      │      TrspSrvc.java`
`        │      │      TrspVehicleTrms.java`
`        │      │      UploadFileRequest.java`
`        │      │      Vehicle.java`
`        │      │      VehicleAvbResource.java`
`        │      │      VehicleDetails.java`
`        │      │      VehicleInfo.java`
`        │      │      VehicleSearchRequest.java`
`        │      │`
`        │      ├─service`
`        │      │      CarrierApiService.java`
`        │      │      ConvertCarrierTransToCsvService.java`
`        │      │      ConvertService.java`
`        │      │      ConvertShipperTransCapacityToCsvService.java`
`        │      │      ConvertVehicleToCsvService.java`
`        │      │      DataHubApiService.java`
`        │      │`
`        │      └─util`
`        │              CarrierApiUtil.java`
`        │              CommonUtil.java`
`        │              DataHubApiUtil.java`
`        │              FileUtil.java`
`        │              JsonUtil.java`
`        │`
`        └─resources`
`                application-dev.properties`
`                application-production.properties`
`                application.properties`
`                logback-spring.xml`

# 使い方

datahub-snd-carrier
	--変数について--
		application.propertiesの
		app.api.carriero-url-auth
		に外部認証API接続先のURLを入力する
		app.api.carrier-url-data-channel
		に外部API接続先のURLのエンドポイント以前のパスを入力する

		vehicle=/vehicle
		shipper_trans_capacity
		carrier_trans_request	
		carrier_trans_capacity
		に外部API用エンドポイントを入力する
		※上記の外部API接続先のURLと合わせてパスが完成する

		app.api.carrier-ke
		app.api.carrier-client-id
		app.api.carrier-client-secret
		に外部API接続用アカウントを入力する

		app.api.datahub-urlにDataHub APIのURLを入力する	


	--JsonToCsv変換について--

	■項目に関して
	objectをModelとしてデータを保持する
	ModelのデータをCSVに変換していく

	■入れ子構造について
	入れ子部分の一番最下層をCSV出力単位とする
	データを保持するModelの　孫要素を子要素が持ち、子要素を親要素が持つことで入れ子構造を実現している

	■繰り返し構造について
	jsonデータのarrayをListとしてModelに持たせる
	CSV出力時、List内の項目をCSVデータとして繰り返しCSV出力する

	■繰り返し構造を入れ子にした繰り返し構造について
	繰り返し構造である親項目の子項目の一つに繰り返し構造があった場合、
	繰り返し以外の子項目のデータを持ち回り、繰り返しCSV出力する


## 問合せ及び要望に関して
本リポジトリは現状は主に配布目的の運用となるため、IssueやPull Requestに関しては受け付けておりません

## ライセンス
このプロジェクトは MITライセンス のもとで公開されています
特筆が無い限り、ソースコードおよび関連ドキュメントの著作権はヤマト運輸株式会社に帰属します

## 免責事項
本リポジトリの内容は予告なく変更・削除する可能性があります
本リポジトリの利用により生じた損失及び損害等について、いかなる責任も負わないものとします
