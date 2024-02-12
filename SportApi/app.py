from datetime import datetime

from flask import Flask, jsonify

app = Flask(__name__)


@app.route('/api/games', methods=['GET'])
def get_games():
    games = [
        {
            'strTeamHome': 'AC Connecticut',
            'strTeamAway': 'AC Houston Sur',
            'strLeague': 'USL League Two',
            'strSport': 'Soccer',
            'strCountry': 'United States',
            'dateOfMatch': '28.12.2024 14:30'
        },
        {
            'strTeamHome': 'Arizona Coyotes',
            'strTeamAway': 'Anaheim Ducks',
            'strLeague': 'NHL',
            'strSport': 'Ice Hockey',
            'strCountry': 'United States',
            'dateOfMatch': '26.12.2024 20:30'
        },
        {
            'strTeamHome': 'Brooklyn Nets',
            'strTeamAway': 'Boston Celtics',
            'strLeague': 'NBA',
            'strSport': 'Basketball',
            'strCountry': 'United States',
            'dateOfMatch': '24.12.2024 19:30'
        },
        {
            'strTeamHome': 'Chicago Blackhawks',
            'strTeamAway': 'Carolina Hurricanes',
            'strLeague': 'NHL',
            'strSport': 'Ice Hockey',
            'strCountry': 'United States',
            'dateOfMatch': '21.12.2024 17:30'
        },
        {
            'strTeamHome': 'CB Ciudad de Valladolid',
            'strTeamAway': 'CB Clavijo',
            'strLeague': 'Spanish LEB Oro',
            'strSport': 'Basketball',
            'strCountry': 'Spain',
            'dateOfMatch': '21.12.2024 17:30'
        },
        {
            'strTeamHome': 'Buffalo Bills',
            'strTeamAway': 'Arizona Cardinals',
            'strLeague': 'NFL',
            'strSport': 'American Football',
            'strCountry': 'United States',
            'dateOfMatch': '23.12.2024 17:30'
        },
        {
            'strTeamHome': 'Paris Musketeers',
            'strTeamAway': 'Barcelona Dragons',
            'strLeague': 'European League of Football',
            'strSport': 'American Football',
            'strCountry': None,
            'dateOfMatch': '23.12.2024 17:30'
        },
        {
            'strTeamHome': 'BSFK Brovary',
            'strTeamAway': 'Kharkiv Berserkers',
            'strLeague': 'Ukrainian UHL',
            'strSport': 'Ice Hockey',
            'strCountry': 'Ukraine',
            'dateOfMatch': '25.12.2024 12:30'
        },
        {
            'strTeamHome': 'Ath Bilbao',
            'strTeamAway': 'Ath Madrid',
            'strLeague': 'Spanish La Liga',
            'strSport': 'Soccer',
            'strCountry': 'Spain',
            'dateOfMatch': '23.12.2024 16:30'
        },
        {
            'strTeamHome': 'BC Budivelnyk',
            'strTeamAway': 'BC Dnipro',
            'strLeague': 'Ukrainian Basketball SuperLeague',
            'strSport': 'Basketball',
            'strCountry': 'Ukraine',
            'dateOfMatch': '23.12.2024 16:30'
        },
        {
            'strTeamHome': 'Colorado Avalanche',
            'strTeamAway': 'Columbus Blue Jackets',
            'strLeague': 'NHL',
            'strSport': 'Ice Hockey',
            'strCountry': 'United States',
            'dateOfMatch': '25.12.2024 21:30'
        },
        {
            'strTeamHome': 'Dallas Stars',
            'strTeamAway': 'Detroit Red Wings',
            'strLeague': 'NHL',
            'strSport': 'Ice Hockey',
            'strCountry': 'United States',
            'dateOfMatch': '23.12.2024 22:00'
        },
        # Add other games similarly
    ]

    return jsonify({'games': games})


if __name__ == '__main__':
    app.run(debug=True)
