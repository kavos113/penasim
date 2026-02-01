proto:
	protoc --plugin=protoc-gen-penasim=protoc/protoc-gen-penasim.bat --penasim_out=out --proto_path=proto/src/main/proto proto/src/main/proto/com/example/penasim/model.proto