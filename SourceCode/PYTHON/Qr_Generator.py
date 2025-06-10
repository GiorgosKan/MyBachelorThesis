import qrcode

def generate_qr_code(data, filename):
    qr = qrcode.QRCode(
        version=1,
        error_correction=qrcode.constants.ERROR_CORRECT_L,
        box_size=10,
        border=4,
    )
    qr.add_data(data)
    qr.make(fit=True)

    img = qr.make_image(fill='black', back_color='white')
    img.save(filename)

# Δημιουργία QR κωδικών για εκθέματα με ID 1, 2, 3
generate_qr_code(30, 'exhibit_30.png')
generate_qr_code(31, 'exhibit_31.png')
generate_qr_code(32, 'exhibit_32.png')
