
var cache: [String: UIImage] = [:]
func createImage(from uri: String?) -> UIImage? {
  guard let uri = uri else { return nil }
  let key = uri
  if let i = cache[key] { return i }
  if let url = URL(string: uri as String),
     let data = try? Data(contentsOf: url),
     let image = UIImage(data: data) {
    cache[key] = image
    return image
  }
  return nil
}

func hexStringToUIColor (hex: String?) -> UIColor? {
    guard let hex = hex else {
        return nil
    }
    var cString = hex.trimmingCharacters(in: .whitespacesAndNewlines).uppercased()
    if (cString.hasPrefix("#")) { cString.remove(at: cString.startIndex) }
    if ((cString.count) != 6) { return UIColor.gray }
    var rgbValue: UInt64 = 0
    Scanner(string: cString).scanHexInt64(&rgbValue)
    
    return UIColor(
        red: CGFloat((rgbValue & 0xFF0000) >> 16) / 255.0,
        green: CGFloat((rgbValue & 0x00FF00) >> 8) / 255.0,
        blue: CGFloat(rgbValue & 0x0000FF) / 255.0,
        alpha: CGFloat(1.0)
    )
}


struct DefaultStyle {
    var cornerRadius: Int = 0
    var errorIcon: UIImage?
    var successBackgroundColor: UIColor?
    var errorBackgroundColor: UIColor?
    var successTitleColor: UIColor?
    var successSubtitleColor: UIColor?
    var errorTitleColor: UIColor?
    var errorSubtitleColor: UIColor?
}

@objc(NotificationBanner)
class NotificationBanner: NSObject {
    var presentedBanner: BaseNotificationBanner?
    var defaultStyle: DefaultStyle?
    
    @objc
    func configure(_ params: [String: Any]) {
        defaultStyle = DefaultStyle(
            cornerRadius: params["cornerRadius"] as? Int ?? 0,
            errorIcon: createImage(from: params["errorIcon"] as? String),
            successBackgroundColor: hexStringToUIColor(hex: params["successBackgroundColor"] as? String),
            errorBackgroundColor: hexStringToUIColor(hex: params["errorBackgroundColor"] as? String),
            successTitleColor: hexStringToUIColor(hex: params["successTitleColor"] as? String),
            errorTitleColor: hexStringToUIColor(hex: params["errorTitleColor"] as? String),
            errorSubtitleColor: hexStringToUIColor(hex: params["errorSubtitleColor"] as? String)
        )
    }
    
    @objc
    func show(_ params: [String: Any], onPress: RCTResponseSenderBlock?) {
        var style: BannerStyle = .success
        let type = params["style"] as? String
        let duration = params["duration"] as? Int ?? 500
        let borderRadius = defaultStyle?.cornerRadius ?? 0
        if type == "error" {
            style = .danger
        }
        
        if type == "info" {
            style = .info
        }
        
        DispatchQueue.main.async { [weak self] in
            guard let self = self else { return }
            self.presentedBanner?.dismiss()
            
            var iconView: UIImageView?
            var titleColor: UIColor?
            var subtitleColor: UIColor?
            var backgroundColor: UIColor?
            
            if style == .danger && self.defaultStyle?.errorIcon != nil && params["icon"] == nil {
                iconView = UIImageView(image: self.defaultStyle!.errorIcon)
            }
            
            if style == .danger && params["icon"] != nil {
                iconView = UIImageView(image: createImage(from: params["icon"] as? String))
            }
            
            if style == .danger && self.defaultStyle?.errorTitleColor != nil {
                titleColor = self.defaultStyle?.errorTitleColor
            }
            
            if style == .danger && self.defaultStyle?.errorSubtitleColor != nil {
                subtitleColor = self.defaultStyle?.errorSubtitleColor
            }
            
            if style == .success && self.defaultStyle?.successTitleColor != nil {
                titleColor = self.defaultStyle?.successTitleColor
            }
            
            if style == .success && self.defaultStyle?.successSubtitleColor != nil {
                subtitleColor = self.defaultStyle?.successSubtitleColor
            }
            
            
            if style == .danger && self.defaultStyle?.errorBackgroundColor != nil {
                backgroundColor = self.defaultStyle?.errorBackgroundColor
            }
            
            if style == .success && self.defaultStyle?.successBackgroundColor != nil {
                backgroundColor = self.defaultStyle?.successBackgroundColor
            }
            
            
            
            let banner = FloatingNotificationBanner(
                title: params["title"] as? String,
                subtitle: params["subtitle"] as? String,
                titleColor: titleColor,
                subtitleColor: subtitleColor,
                leftView: iconView,
                style: style,
                backgroundColor: backgroundColor
            )
            banner.duration = Double(duration) / 1000.0
            banner.show(
                edgeInsets: .init(top: 16, left: 32, bottom: 0, right: 32),
                cornerRadius: CGFloat(borderRadius),
                shadowOpacity: 0.3,
                shadowBlurRadius: 10,
                shadowOffset: .init(horizontal: 3, vertical: 3)
            )
            banner.onTap = {
                onPress?(nil)
            }
            self.presentedBanner = banner
        }
    }
}
