import UIKit

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


@objc(NotificationBanner)
class NotificationBanner: RCTViewManager {
    var presentedBanner: BaseNotificationBanner?
    
    override static func requiresMainQueueSetup() -> Bool {
      return true
    }
    
    @objc
    func configure(_ params: [String: Any]) {
        var errorStyle: ErrorStyle?
        if let error = params["error"] as? [String: Any] {
            var font: Font = .init(size: 16, family: nil)
            if let f = error["titleFont"] as? [String: Any] {
                font = Font(size: f["size"] as? Double ?? 17, family: f["family"] as? String)
            }
            
            let current = bannerAppearance(type: .danger)
            
            errorStyle = ErrorStyle(
                icon: createImage(from: error["icon"] as? String),
                backgroundColor: RCTConvert.uiColor(error["backgroundColor"]) ?? current.backgroundColor,
                titleColor: RCTConvert.uiColor(error["titleColor"]) ?? current.titleColor,
                messageColor: RCTConvert.uiColor(error["messageColor"]) ?? current.messageColor,
                titleFont: font
            )
        }
        
        var successStyle: SuccessStyle?
        if let success = params["success"] as? [String: Any] {
            var font: Font = .init(size: 16, family: nil)
            if let f = success["titleFont"] as? [String: Any] {
                font = Font(size: f["size"] as? Double ?? 17, family: f["family"] as? String)
            }
            
            let current = bannerAppearance(type: .success)
            
            successStyle = SuccessStyle(
                icon: createImage(from: success["icon"] as? String),
                backgroundColor: RCTConvert.uiColor(success["backgroundColor"]) ?? current.backgroundColor,
                titleColor: RCTConvert.uiColor(success["titleColor"]) ?? current.titleColor,
                messageColor: RCTConvert.uiColor(success["messageColor"]) ?? current.messageColor,
                titleFont: font
            )
        }
        
        
        var shadow: Shadow?
        if let s = params["shadow"] as? [String: Any] {
            shadow = .init(
                offset: RCTConvert.cgSize(s["offset"]),
                color: RCTConvert.uiColor(s["color"]),
                radius: RCTConvert.cgFloat(s["radius"]),
                opacity: RCTConvert.float(s["opacity"])
            )
        }
        
        var oldAppearance = currentAppearance
        if let radius = params["cornerRadius"] as? Double {
            oldAppearance.cornerRadius = radius
        } else {
            oldAppearance.cornerRadius = 1000
        }
        
        if let value = params["margin"] as? Double {
            oldAppearance.margin = value
        } else {
            oldAppearance.margin = 32
        }
        
        if let value = params["padding"] as? Double {
            oldAppearance.padding = value
        } else {
            oldAppearance.padding = 16
        }
        
        if let shadow = shadow {
            oldAppearance.shadow = shadow
        }
        
        if let error = errorStyle {
            oldAppearance.error = error
        }
        
        if let success = successStyle {
            oldAppearance.success = success
        }
        
        setNewAppearance(oldAppearance)
    }
    
    @objc
    func show(_ params: [String: Any], onPress: RCTResponseSenderBlock?) {
        var style: BannerStyle = .success
        let type = params["style"] as? String
        let duration = params["duration"] as? Int ?? 500
        
        if type == "error" { style = .danger }
        if type == "info" { style = .info }
        
        let appearance = bannerAppearance(type: style)
        
        DispatchQueue.main.async { [weak self] in
            guard let self = self else { return }
            self.presentedBanner?.dismiss()
            
            var iconView: UIImageView?
            
            if params["icon"] != nil {
                iconView = UIImageView(image: createImage(from: params["icon"] as? String))
            } else if let icon = appearance.icon {
                iconView = UIImageView(image: icon)
            }
            
            let banner = FloatingNotificationBanner(
                title: params["title"] as? String,
                subtitle: params["message"] as? String,
                leftView: iconView,
                style: style,
                backgroundColor: appearance.backgroundColor
            )
            banner.duration = Double(duration) / 1000.0
            banner.show(
                edgeInsets: .init(top: 16, left: currentAppearance.margin, bottom: 0, right: currentAppearance.margin),
                cornerRadius: CGFloat(currentAppearance.cornerRadius),
                shadowOpacity: CGFloat(currentAppearance.shadow.opacity),
                shadowBlurRadius: currentAppearance.shadow.radius,
                shadowOffset: .init(
                    horizontal: currentAppearance.shadow.offset.width,
                    vertical: currentAppearance.shadow.offset.height
                )
            )
            banner.onTap = {
                onPress?(nil)
            }
            self.presentedBanner = banner
        }
    }
}
