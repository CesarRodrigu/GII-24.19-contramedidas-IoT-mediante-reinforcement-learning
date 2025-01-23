
from flask import Blueprint, render_template,url_for

bp = Blueprint('main', __name__)


@bp.route('/')
def home() -> str:
    return render_template('app_main.html')
