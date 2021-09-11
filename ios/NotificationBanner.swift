import NotificationBannerSwift

@objc(NotificationBanner)
class NotificationBanner: NSObject {

    @objc(multiply:withB:withResolver:withRejecter:)
    func multiply(a: Float, b: Float, resolve:RCTPromiseResolveBlock,reject:RCTPromiseRejectBlock) -> Void {
        resolve(a*b)
    }
    
    @objc
    func show(_ params: [String: Any]) -> Void {
        var style: BannerStyle = .success
        let type = params["style"] as? String
        let duration = params["duration"] as? Int ?? 500
        let borderRadius = params["borderRadius"] as? Int ?? 10
        if type == "error" {
            style = .danger
        }
        
        if type == "info" {
            style = .info
        }
        DispatchQueue.main.async {
            let banner = FloatingNotificationBanner(
                title: params["title"] as? String,
                subtitle: params["subtitle"] as? String,
                style: style
            )
            banner.duration = Double(duration) / 1000.0
            banner.show(
                cornerRadius: CGFloat(borderRadius),
                shadowOpacity: 0.3,
                shadowBlurRadius: 10,
                shadowOffset: .init(horizontal: 3, vertical: 3)
            )
        }
    }
}
