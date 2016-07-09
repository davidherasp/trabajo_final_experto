
# coding: utf-8

# In[12]:

import pandas as pd


# In[13]:

games = pd.read_csv("games.csv")
games['summoners'] = games.summoner1.astype(str).str.cat(games.summoner2.astype(str), sep='-')
wins = games.drop(games[games.win == False].index)
loses = games.drop(games[games.win == True].index)


# In[23]:

lanesumgroup = wins.groupby(['lane','summoners'])['win']


# In[24]:

lanesumframe = lanesumgroup.count().to_frame()


# In[25]:

ulsframe = lanesumframe.unstack(0)


# In[26]:

uframefill = ulsframe.fillna(0)


# In[27]:

uframefill.columns=['bottom wins', 'jungle wins', 'mid wins', 'top wins']
uframefill.astype(int).to_csv('lanesumswinratio.csv')



