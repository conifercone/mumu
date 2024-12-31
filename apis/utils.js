// noinspection all

export function generateHeaders(serviceUrl) {
  const requestIdString = $random.uuid
  const timestampString = $timestamp
  const requestPath = request.url.tryGetSubstituted().replace(serviceUrl, "");
  const requestBody = request.body.tryGetSubstituted() == null ? ''
      : request.body.tryGetSubstituted();
  // 转换为紧凑格式的 JSON 字符串
  const compactJsonString = requestBody.length == 0 ? '' : JSON.stringify(
      JSON.parse(requestBody));
  const dataToSign = timestampString + requestIdString + requestPath
      + compactJsonString;
  const signatureString = crypto.hmac.sha256()
  .withTextSecret(request.environment.get("secret"))
  .updateWithText(dataToSign)
  .digest().toHex();
  return {
    timestamp: timestampString,
    requestId: requestIdString,
    signature: signatureString,
  };
}
